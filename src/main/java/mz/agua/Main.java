package mz.agua;

import mz.agua.config.DatabaseConnection;
import mz.agua.controller.LoginController;
import mz.agua.dao.UsuarioDAO;
import mz.agua.exception.PersistenciaException;
import mz.agua.model.Usuario;
import mz.agua.service.AutenticacaoService;
import mz.agua.view.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Arranque da aplicação. A janela de login abre SEMPRE; se a base de dados estiver inacessível
 * ou ainda não existirem contas, o problema aparece como mensagem na própria janela de login.
 */
public final class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /** Uma só instância: a limitação de tentativas de login fica partilhada entre logins. */
    private static final AutenticacaoService AUTENTICACAO = new AutenticacaoService();

    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            configurarAspecto();
            abrirLogin();
        });
    }

    /** Abre a janela de login (pode ser chamado de qualquer thread). */
    public static void abrirLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame janela = new LoginFrame();
            new LoginController(janela, AUTENTICACAO, Main::aoAutenticar);
            janela.setVisible(true);
            verificarAmbienteEmSegundoPlano(janela);
        });
    }

    /**
     * Logout: termina a sessão e volta ao login. O MainFrame (Opção 6) deve fechar-se a si
     * próprio e chamar este método.
     */
    public static void terminarSessaoEVoltarAoLogin() {
        AUTENTICACAO.terminarSessao();
        abrirLogin();
    }

    /** Chamado na EDT depois de um login com sucesso. */
    private static void aoAutenticar(Usuario utilizador) {
        // TODO (integração com a Opção 6): substituir o bloco abaixo por
        //     new MainFrame().setVisible(true);
        // O MainFrame lê a sessão com Sessao.getUsuarioAutenticado() / Sessao.isAdmin().
        JOptionPane.showMessageDialog(null,
                "Sessão iniciada: " + utilizador.getNomeFuncionario() + " (" + utilizador.getPerfil() + ").\n"
                        + "A janela principal (Opção 6) ainda não está integrada.\n"
                        + "Ao fechar esta mensagem a sessão termina e volta ao login.",
                "Sessão iniciada", JOptionPane.INFORMATION_MESSAGE);
        terminarSessaoEVoltarAoLogin();
    }

    /** Verifica a BD fora da EDT e, se houver problema, mostra-o na janela de login. */
    private static void verificarAmbienteEmSegundoPlano(LoginFrame janela) {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return verificarAmbiente();
            }

            @Override
            protected void done() {
                try {
                    String problema = get();
                    if (problema != null) {
                        janela.mostrarErro(problema);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    LOG.log(Level.SEVERE, "Erro inesperado ao verificar o ambiente", e.getCause());
                    janela.mostrarErro("Erro inesperado ao verificar o ambiente. Consulte o registo (consola).");
                }
            }
        }.execute();
    }

    /** null se tudo estiver bem; caso contrário, a mensagem a mostrar ao utilizador. */
    private static String verificarAmbiente() {
        try {
            DatabaseConnection.testarLigacao();
            long contas = DatabaseConnection.executar(c -> new UsuarioDAO(c).contarContas());
            if (contas == 0) {
                return "Ainda não existe nenhuma conta de utilizador.\n"
                        + "Crie o primeiro ADMIN com o utilitário mz.agua.util.CriarAdminInicial.";
            }
            return null;
        } catch (PersistenciaException e) {
            LOG.log(Level.SEVERE, "Falha na verificação do ambiente", e);
            return e.getMessage();
        }
    }

    private static void configurarAspecto() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Não foi possível aplicar o aspecto do sistema", e);
        }
    }
}
