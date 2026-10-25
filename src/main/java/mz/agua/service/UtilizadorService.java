package mz.agua.service;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.UsuarioDAO;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;
import mz.agua.util.PasswordHasher;
import mz.agua.util.Sessao;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Gestão de contas de utilizador (UC12) — só para ADMIN. A permissão é verificada AQUI, não
 * apenas na View. O Controller/Painel desta funcionalidade fica para quem a integrar (Opções 5/6).
 *
 * <p>Os métodos NÃO limpam os arrays de password recebidos: o chamador faz Arrays.fill(..., '\0').
 */
public class UtilizadorService {

    private static final Pattern USERNAME_VALIDO = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final int PASSWORD_MIN_CARACTERES = 8;
    private static final int PASSWORD_MAX_BYTES = 72;   // limite do bcrypt

    // ------------------------------------------------------------------ validações (estáticas)

    /** Devolve o username já sem espaços nas pontas; lança ValidacaoException se inválido. */
    public static String validarUsername(String username) {
        String nome = username == null ? "" : username.trim();
        if (!USERNAME_VALIDO.matcher(nome).matches()) {
            throw new ValidacaoException(
                    "O nome de utilizador deve ter 3 a 50 caracteres (letras, números, '.', '_' ou '-').");
        }
        return nome;
    }

    /**
     * Política de password: 8 a 72 bytes (UTF-8), com pelo menos uma letra e um número, e
     * diferente do username. Não cria Strings com a password (para a poder limpar depois).
     */
    public static void validarPassword(char[] password, String username) {
        if (password == null || password.length < PASSWORD_MIN_CARACTERES) {
            throw new ValidacaoException("A palavra-passe deve ter pelo menos 8 caracteres.");
        }
        ByteBuffer bytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
        int tamanho = bytes.remaining();
        Arrays.fill(bytes.array(), (byte) 0);
        if (tamanho > PASSWORD_MAX_BYTES) {
            throw new ValidacaoException("A palavra-passe é demasiado longa (máximo 72 bytes).");
        }
        boolean temLetra = false;
        boolean temNumero = false;
        for (char c : password) {
            temLetra |= Character.isLetter(c);
            temNumero |= Character.isDigit(c);
        }
        if (!temLetra || !temNumero) {
            throw new ValidacaoException("A palavra-passe deve ter pelo menos uma letra e um número.");
        }
        if (username != null && igualIgnorandoMaiusculas(password, username)) {
            throw new ValidacaoException("A palavra-passe não pode ser igual ao nome de utilizador.");
        }
    }

    private static boolean igualIgnorandoMaiusculas(char[] password, String texto) {
        if (password.length != texto.length()) {
            return false;
        }
        for (int i = 0; i < password.length; i++) {
            if (Character.toLowerCase(password[i]) != Character.toLowerCase(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ operações (ADMIN)

    /** UC12: cria a conta de um funcionário existente que ainda não tem conta. */
    public Usuario criarConta(int idFuncionario, String username, Perfil perfil, char[] passwordInicial) {
        Sessao.exigirAdmin();
        String nome = validarUsername(username);
        if (perfil == null) {
            throw new ValidacaoException("Indique o perfil (ADMIN ou OPERADOR).");
        }
        validarPassword(passwordInicial, nome);
        String hash = PasswordHasher.gerarHash(passwordInicial);

        return DatabaseConnection.emTransaccao(c -> {
            UsuarioDAO dao = new UsuarioDAO(c);
            if (dao.buscarFuncionario(idFuncionario).isEmpty()) {
                throw new ValidacaoException("O funcionário indicado não existe.");
            }
            if (dao.buscarPorFuncionario(idFuncionario).isPresent()) {
                throw new ValidacaoException("Este funcionário já tem uma conta de utilizador.");
            }
            int id = dao.inserir(new Usuario(0, nome, hash, perfil, true, idFuncionario, null, null));
            return dao.buscarPorId(id).orElseThrow().semPasswordHash();
        });
    }

    /** Lista as contas (sem hash). */
    public List<Usuario> listarContas() {
        Sessao.exigirAdmin();
        return DatabaseConnection.executar(c -> new UsuarioDAO(c).listar());
    }

    public void activarConta(int idUtilizador) {
        Sessao.exigirAdmin();
        DatabaseConnection.emTransaccao(c -> {
            new UsuarioDAO(c).actualizarActivo(idUtilizador, true);
            return null;
        });
    }

    /** Desactiva a conta. Não é permitido desactivar a própria conta (evita ficar sem acesso). */
    public void desactivarConta(int idUtilizador) {
        Sessao.exigirAdmin();
        if (idUtilizador == Sessao.getUsuarioAutenticado().getIdUtilizador()) {
            throw new ValidacaoException("Não pode desactivar a sua própria conta.");
        }
        DatabaseConnection.emTransaccao(c -> {
            new UsuarioDAO(c).actualizarActivo(idUtilizador, false);
            return null;
        });
    }

    /** Define uma nova password (reposição por um ADMIN). */
    public void redefinirPassword(int idUtilizador, char[] novaPassword) {
        Sessao.exigirAdmin();
        DatabaseConnection.emTransaccao(c -> {
            UsuarioDAO dao = new UsuarioDAO(c);
            Usuario alvo = dao.buscarPorId(idUtilizador)
                    .orElseThrow(() -> new ValidacaoException("A conta de utilizador indicada não existe."));
            validarPassword(novaPassword, alvo.getUsername());
            dao.actualizarPasswordHash(idUtilizador, PasswordHasher.gerarHash(novaPassword));
            return null;
        });
    }
}
