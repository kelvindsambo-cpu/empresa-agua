package mz.agua.view;

import mz.agua.controller.FaturaController;
import mz.agua.controller.TarifaController;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;
import mz.agua.util.Sessao;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Arranque TEMPORÁRIO para experimentar os ecrãs de tarifas e facturas enquanto o MainFrame
 * (Membro 6) não existe. Usa uma sessão ADMIN fictícia, mas a base de dados é a verdadeira
 * (application.properties). Apagar quando os painéis estiverem ligados ao MainFrame.
 */
public final class ExperimentarFacturacao {

    private ExperimentarFacturacao() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sessao.iniciar(new Usuario(0, "teste", null, Perfil.ADMIN, true, 1, "Teste", "administrador"));

            TarifaPanel tarifas = new TarifaPanel();
            FaturaPanel faturas = new FaturaPanel();
            JTabbedPane separadores = new JTabbedPane();
            separadores.addTab("Tarifas", tarifas);
            separadores.addTab("Facturas", faturas);

            JFrame janela = new JFrame("Experimentar facturação");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(separadores);
            janela.setSize(1000, 600);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);

            new TarifaController(tarifas);
            new FaturaController(faturas);
        });
    }
}
