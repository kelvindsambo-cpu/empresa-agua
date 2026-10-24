package mz.agua.service;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.UsuarioDAO;
import mz.agua.exception.AutenticacaoException;
import mz.agua.model.Usuario;
import mz.agua.util.PasswordHasher;
import mz.agua.util.Sessao;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Início e fim de sessão (UC01).
 *
 * <ul>
 *   <li>Mensagem única «Credenciais inválidas.» para username inexistente, password errada e
 *       conta inactiva: não revela se o utilizador existe.</li>
 *   <li>O hash é verificado mesmo quando o username não existe, para o tempo de resposta
 *       também não o revelar.</li>
 *   <li>Depois de 5 falhas seguidas para o mesmo username, bloqueia esse username durante
 *       60 segundos (em memória, por execução da aplicação).</li>
 *   <li>Nada sensível é registado em log (nem passwords, nem hashes, nem o username digitado).</li>
 * </ul>
 *
 * Este método é lento (bcrypt + BD): o Controller chama-o fora da EDT (SwingWorker).
 */
public class AutenticacaoService {

    public static final String MSG_CREDENCIAIS_INVALIDAS = "Credenciais inválidas.";

    private static final Logger LOG = Logger.getLogger(AutenticacaoService.class.getName());
    private static final int MAX_FALHAS = 5;
    private static final Duration BLOQUEIO = Duration.ofSeconds(60);
    private static final int MAX_USERNAMES_EM_MEMORIA = 1000;

    private static final class Tentativas {
        int falhas;
        Instant bloqueadoAte;
    }

    private final Map<String, Tentativas> tentativas = new HashMap<>();   // protegido por "this"
    private final Clock relogio;

    public AutenticacaoService() {
        this(Clock.systemUTC());
    }

    /** Visível ao pacote para os testes poderem controlar o relógio. */
    AutenticacaoService(Clock relogio) {
        this.relogio = relogio;
        // Calcula já o hash fictício (~250 ms, uma vez): assim a primeira tentativa com um
        // username inexistente não demora mais do que as seguintes.
        PasswordHasher.hashFicticio();
    }

    /**
     * Autentica e inicia a sessão.
     *
     * @param password o chamador é responsável por limpar o array depois da chamada
     * @return o utilizador autenticado, sem hash
     * @throws AutenticacaoException credenciais inválidas, conta inactiva ou bloqueio temporário
     */
    public Usuario autenticar(String username, char[] password) {
        String nome = username == null ? "" : username.trim();
        if (nome.isEmpty() || password == null || password.length == 0) {
            throw new AutenticacaoException(MSG_CREDENCIAIS_INVALIDAS);
        }
        verificarBloqueio(nome);

        Optional<Usuario> encontrado =
                DatabaseConnection.executar(c -> new UsuarioDAO(c).buscarPorUsername(nome));

        String hash = encontrado.map(Usuario::getPasswordHash).orElseGet(PasswordHasher::hashFicticio);
        boolean passwordCorrecta = PasswordHasher.verificar(password, hash);

        if (encontrado.isEmpty() || !passwordCorrecta) {
            registarFalha(nome);
            throw new AutenticacaoException(MSG_CREDENCIAIS_INVALIDAS);
        }

        Usuario conta = encontrado.get();
        if (!conta.isActivo()) {
            LOG.info("Tentativa de início de sessão numa conta inactiva (id_utilizador="
                    + conta.getIdUtilizador() + ").");
            throw new AutenticacaoException(MSG_CREDENCIAIS_INVALIDAS);
        }

        limparFalhas(nome);
        Usuario paraSessao = conta.semPasswordHash();
        Sessao.iniciar(paraSessao);
        return paraSessao;
    }

    /** Termina a sessão (logout). */
    public void terminarSessao() {
        Sessao.terminarSessao();
    }

    // ------------------------------------------------------------------ limitação de tentativas

    private static String chave(String nome) {
        String k = nome.toLowerCase(Locale.ROOT);
        return k.length() > 100 ? k.substring(0, 100) : k;
    }

    private synchronized void verificarBloqueio(String nome) {
        Tentativas t = tentativas.get(chave(nome));
        if (t == null || t.bloqueadoAte == null) {
            return;
        }
        Instant agora = relogio.instant();
        if (agora.isBefore(t.bloqueadoAte)) {
            long segundos = Duration.between(agora, t.bloqueadoAte).toSeconds() + 1;
            throw new AutenticacaoException(
                    "Demasiadas tentativas falhadas. Tente novamente dentro de " + segundos + " segundos.");
        }
        tentativas.remove(chave(nome));   // o bloqueio expirou: recomeça a contagem
    }

    private synchronized void registarFalha(String nome) {
        if (tentativas.size() >= MAX_USERNAMES_EM_MEMORIA) {
            tentativas.clear();   // limite de memória (a aplicação é local e de curta duração)
        }
        Tentativas t = tentativas.computeIfAbsent(chave(nome), k -> new Tentativas());
        t.falhas++;
        if (t.falhas >= MAX_FALHAS) {
            t.bloqueadoAte = relogio.instant().plus(BLOQUEIO);
        }
    }

    private synchronized void limparFalhas(String nome) {
        tentativas.remove(chave(nome));
    }
}
