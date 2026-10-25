package mz.agua.view;

import mz.agua.model.Fatura;
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

/** Lista de facturas. Sem SQL: o FaturaController preenche a tabela e trata os botões. */
@SuppressWarnings("serial")
public class FaturaPanel extends JPanel {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] {"Nº", "Cliente", "Contador", "Referência", "Leitura anterior", "Leitura actual",
                    "Consumo (m³)", "Valor (MT)", "Vencimento", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JButton botaoEmitir = new JButton("Emitir factura");
    private final JButton botaoActualizar = new JButton("Actualizar");

    public FaturaPanel() {
        super(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Facturas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoActualizar);
        botoes.add(botaoEmitir);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    public void mostrarFaturas(List<Fatura> faturas) {
        modelo.setRowCount(0);
        for (Fatura f : faturas) {
            modelo.addRow(new Object[] {
                    f.getIdFatura(),
                    f.getNomeCliente(),
                    f.getNumeroContador(),
                    String.format("%02d/%d", f.getMesReferencia(), f.getAnoReferencia()),
                    f.getLeituraAnterior(),
                    f.getLeituraAtual(),
                    f.getConsumo(),
                    f.getValorTotal(),
                    Formatos.data(f.getDataVencimento()),
                    f.getEstadoFatura()
            });
        }
    }

    public void addEmitirListener(ActionListener listener) { botaoEmitir.addActionListener(listener); }
    public void addActualizarListener(ActionListener listener) { botaoActualizar.addActionListener(listener); }

    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Facturas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
