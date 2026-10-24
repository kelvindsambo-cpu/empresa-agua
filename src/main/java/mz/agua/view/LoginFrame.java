package mz.agua.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * Janela de início de sessão. Só apresentação: lê os campos e mostra mensagens.
 * Não tem SQL nem regras; a lógica está em LoginController/AutenticacaoService.
 * Tem de ser criada e usada na EDT.
 */
@SuppressWarnings({"serial", "this-escape"})
public class LoginFrame extends JFrame {

    private final JTextField campoUsername = new JTextField(20);
    private final JPasswordField campoPassword = new JPasswordField(20);
    private final JButton botaoEntrar = new JButton("Entrar");
    private final JLabel rotuloErro = new JLabel(" ");

    public LoginFrame() {
        super("Fornecimento de Água — Início de sessão");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterface();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(botaoEntrar);   // Enter = Entrar
    }

    private void construirInterface() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridwidth = 2;

        JLabel titulo = new JLabel("Sistema de Gestão de Fornecimento de Água");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        g.gridy = 0;
        g.insets = new Insets(0, 4, 16, 4);
        painel.add(titulo, g);

        g.gridwidth = 1;
        g.insets = new Insets(4, 4, 4, 4);

        g.gridy = 1; g.gridx = 0;
        painel.add(new JLabel("Utilizador:"), g);
        g.gridx = 1;
        painel.add(campoUsername, g);

        g.gridy = 2; g.gridx = 0;
        painel.add(new JLabel("Palavra-passe:"), g);
        g.gridx = 1;
        painel.add(campoPassword, g);

        rotuloErro.setForeground(new Color(0xB00020));
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        painel.add(rotuloErro, g);

        g.gridy = 4;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        painel.add(botaoEntrar, g);

        setContentPane(painel);
    }

    // ------------------------------------------------------------------ API para o Controller

    public void addEntrarListener(ActionListener listener) {
        botaoEntrar.addActionListener(listener);
    }

    public String getUsername() {
        return campoUsername.getText().trim();
    }

    /** Cópia dos caracteres digitados; quem a recebe deve limpá-la (Arrays.fill). */
    public char[] getPassword() {
        return campoPassword.getPassword();
    }

    public void limparPassword() {
        campoPassword.setText("");
    }

    /** Mostra uma mensagem (com quebra de linha automática) e ajusta o tamanho da janela. */
    public void mostrarErro(String mensagem) {
        String seguro = mensagem.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\n", "<br>");
        rotuloErro.setText("<html><body style='width: 280px'>" + seguro + "</body></html>");
        pack();
    }

    public void limparErro() {
        rotuloErro.setText(" ");
    }

    /** Desactiva os campos enquanto a autenticação decorre. */
    public void setOcupado(boolean ocupado) {
        campoUsername.setEnabled(!ocupado);
        campoPassword.setEnabled(!ocupado);
        botaoEntrar.setEnabled(!ocupado);
        botaoEntrar.setText(ocupado ? "A entrar..." : "Entrar");
        if (!ocupado) {
            (campoUsername.getText().isEmpty() ? campoUsername : campoPassword).requestFocusInWindow();
        }
    }
}
