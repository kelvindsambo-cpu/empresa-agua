package mz.agua.view;

import mz.agua.model.Tarifa;
import mz.agua.util.Formatos;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

/** Lista de tarifas. Sem SQL: o TarifaController preenche a tabela e trata os botões. */
@SuppressWarnings("serial")
public class TarifaPanel extends JPanel {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] {"Tipo", "Preço por m³ (MT)", "Consumo mínimo (m³)", "Início", "Fim"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JButton botaoNova = new JButton("Nova tarifa");
    private final JButton botaoActualizar = new JButton("Actualizar");

    public TarifaPanel() {
        super(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Tarifas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoActualizar);
        botoes.add(botaoNova);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    public void mostrarTarifas(List<Tarifa> tarifas) {
        modelo.setRowCount(0);
        for (Tarifa t : tarifas) {
            modelo.addRow(new Object[] {
                    t.getTipoContrato(),
                    t.getPrecoPorM3(),
                    t.getConsumoMinimo(),
                    Formatos.data(t.getDataInicio()),
                    t.isEmVigor() ? "em vigor" : Formatos.data(t.getDataFim())
            });
        }
    }

    /** Só o ADMIN cria tarifas (UC05-A). */
    public void setNovaTarifaVisivel(boolean visivel) { botaoNova.setVisible(visivel); }

    public void addNovaTarifaListener(ActionListener listener) { botaoNova.addActionListener(listener); }
    public void addActualizarListener(ActionListener listener) { botaoActualizar.addActionListener(listener); }

    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Tarifas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
