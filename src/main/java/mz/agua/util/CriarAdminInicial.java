package mz.agua.util;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.UsuarioDAO;
import mz.agua.dao.UsuarioDAO.FuncionarioBasico;
import mz.agua.exception.PersistenciaException;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;
import mz.agua.service.UtilizadorService;

import java.io.Console;
import java.util.Arrays;
import java.util.Optional;

/**
 * Utilitário de linha de comandos que cria a conta ADMIN inicial (a tabela utilizador começa vazia
 * e não há passwords no SQL versionado).
 *
 * <p>Passos: valida que o funcionário existe e ainda não tem conta, pede confirmação, pede o
 * username e a password (sem eco, duas vezes), gera o hash bcrypt e grava a conta.
 *
 * <p>Uso (num terminal real; num IDE a consola interactiva não existe):
 * <pre>
 *   mvn -q exec:java -Dexec.mainClass=mz.agua.util.CriarAdminInicial -Dexec.args="6"
 * </pre>
 * O argumento é o id_funcionario; se for omitido, é pedido. Não usa sessão: só deve ser
 * executado por quem já tem acesso às credenciais da base de dados.
 */
public final class CriarAdminInicial {

    private static final int TENTATIVAS_PASSWORD = 3;

    private CriarAdminInicial() { }

    public static void main(String[] args) {
        Console consola = System.console();
        if (consola == null) {
            System.err.println("Execute este utilitário num terminal interactivo: é preciso ler a "
                    + "palavra-passe sem a mostrar no ecrã (num IDE a consola não está disponível).");
            System.exit(2);
        }
        System.exit(executar(consola, args));
    }

    private static int executar(Console consola, String[] args) {
        char[] password = null;
        char[] confirmacao = null;
        try {
            int idFuncionario = lerIdFuncionario(consola, args);

            UsuarioDAO.FuncionarioBasico funcionario = DatabaseConnection
                    .executar(c -> new UsuarioDAO(c).buscarFuncionario(idFuncionario))
                    .orElseThrow(() -> new ValidacaoException("Não existe nenhum funcionário com id " + idFuncionario + "."));

            Optional<Usuario> existente = DatabaseConnection
                    .executar(c -> new UsuarioDAO(c).buscarPorFuncionario(idFuncionario));
            if (existente.isPresent()) {
                throw new ValidacaoException("O funcionário " + funcionario.nome()
                        + " já tem uma conta (utilizador '" + existente.get().getUsername() + "').");
            }

            consola.printf("Funcionário: %s (id %d, cargo: %s)%n",
                    funcionario.nome(), funcionario.idFuncionario(), funcionario.cargo());
            String resposta = consola.readLine("Criar uma conta com perfil ADMIN para este funcionário? [s/N]: ");
            if (resposta == null || !resposta.trim().equalsIgnoreCase("s")) {
                System.out.println("Operação cancelada.");
                return 1;
            }

            String username = UtilizadorService.validarUsername(consola.readLine("Nome de utilizador: "));

            for (int tentativa = 1; ; tentativa++) {
                password = consola.readPassword("Palavra-passe: ");
                confirmacao = consola.readPassword("Repita a palavra-passe: ");
                try {
                    if (password == null || confirmacao == null || !Arrays.equals(password, confirmacao)) {
                        throw new ValidacaoException("As palavras-passe não coincidem.");
                    }
                    UtilizadorService.validarPassword(password, username);
                    break;
                } catch (ValidacaoException e) {
                    if (tentativa >= TENTATIVAS_PASSWORD) {
                        throw e;
                    }
                    System.err.println(e.getMessage() + " Tente novamente.");
                    limpar(password);
                    limpar(confirmacao);
                }
            }

            String hash = PasswordHasher.gerarHash(password);
            int idUtilizador = DatabaseConnection.emTransaccao(c -> {
                UsuarioDAO dao = new UsuarioDAO(c);
                // Verificação repetida dentro da transacção; as UNIQUE da BD fecham a corrida.
                if (dao.buscarPorFuncionario(idFuncionario).isPresent()) {
                    throw new ValidacaoException("Este funcionário já tem uma conta de utilizador.");
                }
                return dao.inserir(new Usuario(0, username, hash, Perfil.ADMIN, true, idFuncionario, null, null));
            });

            System.out.printf("Conta ADMIN criada: utilizador '%s' (id_utilizador=%d) para %s.%n",
                    username, idUtilizador, funcionario.nome());
            return 0;

        } catch (ValidacaoException e) {
            System.err.println("Erro: " + e.getMessage());
            return 1;
        } catch (PersistenciaException e) {
            System.err.println("Erro de base de dados: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Detalhe técnico: " + e.getCause().getMessage());
            }
            return 3;
        } finally {
            limpar(password);
            limpar(confirmacao);
        }
    }

    private static int lerIdFuncionario(Console consola, String[] args) {
        String texto = (args.length > 0) ? args[0] : consola.readLine("id_funcionario do futuro ADMIN: ");
        try {
            return Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O id_funcionario deve ser um número inteiro.");
        }
    }

    private static void limpar(char[] valor) {
        if (valor != null) {
            Arrays.fill(valor, '\0');
        }
    }
}
