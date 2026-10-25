package mz.agua.view;

import mz.agua.util.Formatos;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/** Formulário "Nova tarifa". Devolve o texto escrito; o TarifaController converte e valida. */
@SuppressWarnings("serial")
public class TarifaDialog extends JDialog {

    private final JComboBox<String> comboTipo = new JComboBox<>(new String[] {"residencial", "comercial"});
    private final JTextField campoPreco = new JTextField(12);
    private final JTextField campoConsumoMinimo = new JTextField("5.00", 12);
    private final JTextField campoDataInicio = new JTextField(Formatos.data(LocalDate.now()), 12);
    private final JButton botaoGuardar = new JButton("Guardar");
    private final JButton botaoCancelar = new JButton("Cancelar");

    public TarifaDialog(Window dono) {
        super(dono, "Nova tarifa", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(new EmptyBorder(15, 15, 5, 15));
        adicionarLinha(formulario, 0, "Tipo de contrato:", comboTipo);
        adicionarLinha(formulario, 1, "Preço por m³ (MT):", campoPreco);
        adicionarLinha(formulario, 2, "Consumo mínimo (m³):", campoConsumoMinimo);
        adicionarLinha(formulario, 3, "Data de início (dd/mm/aaaa):", campoDataInicio);

        JLabel nota = new JLabel("<html><i>A tarifa em vigor do mesmo tipo termina na véspera desta data.</i></html>");
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 4;
        g.gridwidth = 2;
        g.insets = new Insets(8, 4, 4, 4);
        formulario.add(nota, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCancelar);
        botoes.add(botaoGuardar);
        botaoCancelar.addActionListener(e -> dispose());

        add(formulario, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(botaoGuardar);
        pack();
        setLocationRelativeTo(dono);
    }

    private static void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = linha;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        painel.add(new JLabel(rotulo), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, g);
    }

    public String getTipoContrato() { return (String) comboTipo.getSelectedItem(); }
    public String getPreco() { return campoPreco.getText(); }
    public String getConsumoMinimo() { return campoConsumoMinimo.getText(); }
    public String getDataInicio() { return campoDataInicio.getText(); }

    public void addGuardarListener(ActionListener listener) { botaoGuardar.addActionListener(listener); }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
