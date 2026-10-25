package mz.agua.view;

import mz.agua.dao.FaturaDAO.ContratoResumo;
import mz.agua.dao.FaturaDAO.TecnicoResumo;
import mz.agua.util.Formatos;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Formulário de emissão de factura (UC06): contrato → contador e leitura anterior → leitura
 * actual → "Calcular" mostra o resumo → "Emitir" grava. Sem SQL nem cálculos: tudo no Controller.
 */
@SuppressWarnings("serial")
public class EmissaoFaturaDialog extends JDialog {

    private final JComboBox<ContratoResumo> comboContrato = new JComboBox<>();
    private final JLabel rotuloContador = new JLabel("—");
    private final JLabel rotuloLeituraAnterior = new JLabel("—");
    private final JComboBox<Integer> comboMes = new JComboBox<>();
    private final JComboBox<Integer> comboAno = new JComboBox<>();
    private final JTextField campoDataLeitura = new JTextField(Formatos.data(LocalDate.now()), 12);
    private final JTextField campoLeituraAtual = new JTextField(12);
    private final JComboBox<TecnicoResumo> comboTecnico = new JComboBox<>();
    private final JTextArea areaResumo = new JTextArea(7, 40);
    private final JButton botaoCalcular = new JButton("Calcular");
    private final JButton botaoEmitir = new JButton("Emitir");
    private final JButton botaoCancelar = new JButton("Cancelar");

    public EmissaoFaturaDialog(Window dono) {
        super(dono, "Emitir factura", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        LocalDate hoje = LocalDate.now();
        for (int mes = 1; mes <= 12; mes++) {
            comboMes.addItem(mes);
        }
        comboMes.setSelectedItem(hoje.getMonthValue());
        for (int ano = hoje.getYear() - 2; ano <= hoje.getYear(); ano++) {
            comboAno.addItem(ano);
        }
        comboAno.setSelectedItem(hoje.getYear());

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(new EmptyBorder(15, 15, 5, 15));
        adicionarLinha(formulario, 0, "Contrato:", comboContrato);
        adicionarLinha(formulario, 1, "Contador activo:", rotuloContador);
        adicionarLinha(formulario, 2, "Leitura anterior (m³):", rotuloLeituraAnterior);
        JPanel referencia = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        referencia.add(comboMes);
        referencia.add(new JLabel(" / "));
        referencia.add(comboAno);
        adicionarLinha(formulario, 3, "Mês de referência:", referencia);
        adicionarLinha(formulario, 4, "Data da leitura (dd/mm/aaaa):", campoDataLeitura);
        adicionarLinha(formulario, 5, "Leitura actual (m³):", campoLeituraAtual);
        adicionarLinha(formulario, 6, "Técnico de leitura:", comboTecnico);

        areaResumo.setEditable(false);
        areaResumo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaResumo.setBorder(new TitledBorder("Resumo"));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCancelar);
        botoes.add(botaoCalcular);
        botoes.add(botaoEmitir);
        botaoCancelar.addActionListener(e -> dispose());

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBorder(new EmptyBorder(0, 15, 0, 15));
        centro.add(areaResumo, BorderLayout.CENTER);

        add(formulario, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
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
        g.weightx = 1;
        painel.add(campo, g);
    }

    public void setContratos(List<ContratoResumo> contratos) {
        contratos.forEach(comboContrato::addItem);
    }

    public void setTecnicos(List<TecnicoResumo> tecnicos) {
        tecnicos.forEach(comboTecnico::addItem);
    }

    public void mostrarContador(String numeroContador, BigDecimal leituraAnterior) {
        rotuloContador.setText(numeroContador);
        rotuloLeituraAnterior.setText(leituraAnterior.toPlainString());
        areaResumo.setText("");
    }

    public void mostrarSemContador(String motivo) {
        rotuloContador.setText(motivo);
        rotuloLeituraAnterior.setText("—");
        areaResumo.setText("");
    }

    public ContratoResumo getContrato() { return (ContratoResumo) comboContrato.getSelectedItem(); }
    public TecnicoResumo getTecnico() { return (TecnicoResumo) comboTecnico.getSelectedItem(); }
    public int getMes() { return (Integer) comboMes.getSelectedItem(); }
    public int getAno() { return (Integer) comboAno.getSelectedItem(); }
    public String getDataLeitura() { return campoDataLeitura.getText(); }
    public String getLeituraAtual() { return campoLeituraAtual.getText(); }

    public void mostrarResumo(String resumo) { areaResumo.setText(resumo); }

    public void addContratoListener(ActionListener listener) { comboContrato.addActionListener(listener); }
    public void addCalcularListener(ActionListener listener) { botaoCalcular.addActionListener(listener); }
    public void addEmitirListener(ActionListener listener) { botaoEmitir.addActionListener(listener); }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
