package mz.agua.controller;

import mz.agua.exception.AutenticacaoException;
import mz.agua.exception.PersistenciaException;
import mz.agua.model.Usuario;
import mz.agua.service.AutenticacaoService;
import mz.agua.view.LoginFrame;

import javax.swing.SwingWorker;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Liga a LoginFrame ao AutenticacaoService. Sem SQL. A autenticação (bcrypt + BD) corre num
 * SwingWorker para não bloquear a EDT; a View só é tocada na EDT (done()).
 */
public class LoginController {

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    private final LoginFrame view;
    private final AutenticacaoService servico;
    private final Consumer<Usuario> aoAutenticar;

    /**
     * @param aoAutenticar chamado na EDT, depois de a janela de login fechar, com o utilizador
     *                     autenticado (é aqui que se abre o MainFrame)
     */
    public LoginController(LoginFrame view, AutenticacaoService servico, Consumer<Usuario> aoAutenticar) {
        this.view = view;
        this.servico = servico;
        this.aoAutenticar = aoAutenticar;
        view.addEntrarListener(e -> entrar());
    }

    private void entrar() {
        String username = view.getUsername();
        char[] password = view.getPassword();
        view.limparErro();
        view.setOcupado(true);

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() {
                try {
                    return servico.autenticar(username, password);
                } finally {
                    Arrays.fill(password, '\0');
                }
            }

            @Override
            protected void done() {
                view.setOcupado(false);
                view.limparPassword();
                try {
                    Usuario utilizador = get();
                    view.dispose();
                    aoAutenticar.accept(utilizador);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    tratarErro(e.getCause());
                }
            }
        }.execute();
    }

    private void tratarErro(Throwable causa) {
        if (causa instanceof AutenticacaoException) {
            view.mostrarErro(causa.getMessage());
        } else if (causa instanceof PersistenciaException) {
            LOG.log(Level.SEVERE, "Erro de base de dados durante o início de sessão", causa);
            view.mostrarErro("Não foi possível comunicar com a base de dados. Contacte o administrador.");
        } else {
            LOG.log(Level.SEVERE, "Erro inesperado durante o início de sessão", causa);
            view.mostrarErro("Ocorreu um erro inesperado. Tente novamente.");
        }
    }
}
