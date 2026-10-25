package mz.agua.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * Janela de início de sessão moderna baseada no design fornecido.
 * Versão simplificada: Sem "Recuperar Senha" e sem "Criar Conta".
 * Mantém a API original para o Controller.
 */
@SuppressWarnings({"serial", "this-escape"})
public class LoginFrame extends JFrame {

    // Cores do Design
    private final Color AZUL_PRINCIPAL = new Color(0, 123, 255); // #007BFF
    private final Color AZUL_HOVER = new Color(0, 105, 217);     // Tom mais escuro
    private final Color CINZA_FUNDO_CAMPO = new Color(240, 242, 245); // #F0F2F5
    private final Color CINZA_BORDA = new Color(216, 222, 228);      // #D8DEE4
    private final Color CINZA_TEXTO_DICA = new Color(108, 117, 125); // #6C757D
    private final Color COR_ERRO = new Color(220, 53, 69);            // #DC3545

    // Componentes estilizados
    private final CampoTextoArredondado campoUsername;
    private final CampoSenhaArredondado campoPassword;
    private final BotaoArredondado botaoEntrar;
    private final JLabel rotuloErro = new JLabel(" ");

    // Gerenciador de layout principal
    private final JPanel painelConteudo;

    public LoginFrame() {
        super("Fornecimento de Água — Início de sessão");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Define a fonte padrão como Sans-Serif moderna
        definirFontePadrao(new Font("Segoe UI", Font.PLAIN, 14));

        // Inicializa os componentes estilizados
        campoUsername = new CampoTextoArredondado(20, "icon_user.png");
        campoPassword = new CampoSenhaArredondado(20, "icon_lock.png");
        botaoEntrar = new BotaoArredondado("Entrar");

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(new Color(248, 249, 250)); // Fundo muito claro
        painelConteudo.setBorder(new EmptyBorder(40, 40, 40, 40)); // Margem externa

        construirInterface();
        setContentPane(painelConteudo);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(botaoEntrar);
    }

    private void definirFontePadrao(Font fonte) {
        UIManager.put("Label.font", fonte);
        UIManager.put("Button.font", fonte.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", fonte);
        UIManager.put("PasswordField.font", fonte);
    }

    private void construirInterface() {
        // Painel central branco arredondado
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setBackground(Color.WHITE);
        painelCentral.setBorder(BorderFactory.createCompoundBorder(
                new BordaArredondada(20, CINZA_BORDA, 1),
                new EmptyBorder(40, 40, 40, 40) // Padding interno aumentado para equilibrar
        ));

        // 1. Logo e Nome do Sistema
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelLogo.setBackground(Color.WHITE);
        JLabel labelLogo = carregarImagem("logo_agua.png");
        if (labelLogo == null) {
            JLabel labelFallback = new JLabel("<html><center><b style='color:#007BFF; font-size:18px;'>Água Viva</b><br>" +
                    "<span style='color:#6C757D; font-size:10px;'>Sistema de Fornecimento</span></center></html>");
            painelLogo.add(labelFallback);
        } else {
            painelLogo.add(labelLogo);
        }
        painelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(painelLogo);
        painelCentral.add(Box.createVerticalStrut(25));

        // 2. Título "Acessar Sistema"
        JLabel tituloAcessar = new JLabel("Acessar Sistema");
        tituloAcessar.setFont(tituloAcessar.getFont().deriveFont(Font.BOLD, 18f));
        tituloAcessar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(tituloAcessar);
        painelCentral.add(Box.createVerticalStrut(30));

        // 3. Área de Inputs
        JPanel painelInputs = new JPanel(new GridBagLayout());
        painelInputs.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 8, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1.0;

        // Label e Campo: Utilizador
        JLabel labelUser = new JLabel("Nome de Usuario");
        labelUser.setForeground(new Color(51, 51, 51));
        g.gridy = 0; painelInputs.add(labelUser, g);
        g.gridy = 1; painelInputs.add(campoUsername, g);
        painelInputs.add(Box.createVerticalStrut(15), g);

        // Label e Campo: Senha
        JLabel labelSenha = new JLabel("Senha");
        labelSenha.setForeground(new Color(51, 51, 51));
        g.gridy = 3; painelInputs.add(labelSenha, g);
        g.gridy = 4; painelInputs.add(campoPassword, g);

        painelInputs.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(painelInputs);

        // --- REMOVIDO: "Esqueceu a senha?" ---
        // Adicionado um espaço menor aqui para colar o botão à senha, como no design original
        painelCentral.add(Box.createVerticalStrut(20));

        // 4. Botão "Entrar"
        botaoEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoEntrar.setPreferredSize(new Dimension(320, 45));
        botaoEntrar.setMaximumSize(new Dimension(320, 45));
        painelCentral.add(botaoEntrar);
        painelCentral.add(Box.createVerticalStrut(15));

        // 5. Rótulo de Erro
        rotuloErro.setForeground(COR_ERRO);
        rotuloErro.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(rotuloErro);

        // --- REMOVIDO: "Criar conta" ---
        // Aumentado o espaço final para Ajuda
        painelCentral.add(Box.createVerticalStrut(20));

        // 6. "Ajuda/Suporte" (Mantido, pois é útil mesmo sem auto-serviço)
        JLabel labelAjuda = new JLabel("Ajuda/Suporte");
        labelAjuda.setForeground(CINZA_TEXTO_DICA);
        labelAjuda.setFont(labelAjuda.getFont().deriveFont(12f));
        labelAjuda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelAjuda.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(labelAjuda);

        painelConteudo.add(painelCentral, BorderLayout.CENTER);
    }

    // --- Componentes Customizados (Sem alterações na lógica de desenho) ---

    private JLabel carregarImagem(String nomeArquivo) {
        String caminhoBase = System.getProperty("user.dir") + File.separator;
        String caminhoCompleto = caminhoBase + nomeArquivo;
        File file = new File(caminhoCompleto);
        if (file.exists()) {
            return new JLabel(new ImageIcon(caminhoCompleto));
        }
        return null;
    }

    class BordaArredondada extends AbstractBorder {
        private int raio; private Color cor; private int espessura;
        BordaArredondada(int raio, Color cor, int espessura) { this.raio = raio; this.cor = cor; this.espessura = espessura; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.setStroke(new BasicStroke(espessura));
            g2d.draw(new RoundRectangle2D.Double(x + espessura/2.0, y + espessura/2.0, width - espessura, height - espessura, raio, raio));
            g2d.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(espessura + 2, espessura + 2, espessura + 2, espessura + 2); }
    }

    class CampoTextoArredondado extends JTextField {
        private Icon iconeAtivo; private final int raio = 10;
        CampoTextoArredondado(int colunas, String nomeIcone) {
            super(colunas);
            setOpaque(false); setBackground(CINZA_FUNDO_CAMPO); setCaretColor(AZUL_PRINCIPAL);
            String caminhoBase = System.getProperty("user.dir") + File.separator;
            if (new File(caminhoBase + nomeIcone).exists()) { iconeAtivo = new ImageIcon(caminhoBase + nomeIcone); }
            int paddingEsquerdo = (iconeAtivo != null) ? 35 : 10;
            setBorder(new EmptyBorder(10, paddingEsquerdo, 10, 10));
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) { repaint(); }
                public void focusLost(java.awt.event.FocusEvent evt) { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, raio, raio);
            g2d.setColor(hasFocus() ? AZUL_PRINCIPAL : CINZA_BORDA);
            g2d.setStroke(new BasicStroke(hasFocus() ? 2 : 1));
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, raio, raio);
            if (iconeAtivo != null) {
                int yIcone = (getHeight() - iconeAtivo.getIconHeight()) / 2;
                iconeAtivo.paintIcon(this, g2d, 10, yIcone);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    class CampoSenhaArredondado extends JPasswordField {
        private Icon iconeAtivo; private final int raio = 10;
        CampoSenhaArredondado(int colunas, String nomeIcone) {
            super(colunas);
            setOpaque(false); setBackground(CINZA_FUNDO_CAMPO); setCaretColor(AZUL_PRINCIPAL); setEchoChar('●');
            String caminhoBase = System.getProperty("user.dir") + File.separator;
            if (new File(caminhoBase + nomeIcone).exists()) { iconeAtivo = new ImageIcon(caminhoBase + nomeIcone); }
            int paddingEsquerdo = (iconeAtivo != null) ? 35 : 10;
            setBorder(new EmptyBorder(10, paddingEsquerdo, 10, 10));
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) { repaint(); }
                public void focusLost(java.awt.event.FocusEvent evt) { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, raio, raio);
            g2d.setColor(hasFocus() ? AZUL_PRINCIPAL : CINZA_BORDA);
            g2d.setStroke(new BasicStroke(hasFocus() ? 2 : 1));
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, raio, raio);
            if (iconeAtivo != null) {
                int yIcone = (getHeight() - iconeAtivo.getIconHeight()) / 2;
                iconeAtivo.paintIcon(this, g2d, 10, yIcone);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    class BotaoArredondado extends JButton {
        private final int raio = 10;
        BotaoArredondado(String texto) {
            super(texto);
            setOpaque(false); setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(Color.WHITE); setBackground(AZUL_PRINCIPAL);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setBackground(AZUL_HOVER); }
                @Override public void mouseExited(MouseEvent e) { setBackground(AZUL_PRINCIPAL); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, raio, raio);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------------ API ORIGINAL (MANTIDA)

    public void addEntrarListener(ActionListener listener) { botaoEntrar.addActionListener(listener); }
    public String getUsername() { return campoUsername.getText().trim(); }
    public char[] getPassword() { return campoPassword.getPassword(); }
    public void limparPassword() { campoPassword.setText(""); }
    public void mostrarErro(String mensagem) {
        String seguro = mensagem.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        rotuloErro.setText("<html><body style='width: 250px; text-align: center; font-size: 11px;'>" + seguro + "</body></html>");
        pack();
    }
    public void limparErro() { rotuloErro.setText(" "); }
    public void setOcupado(boolean ocupado) {
        campoUsername.setEnabled(!ocupado); campoPassword.setEnabled(!ocupado); botaoEntrar.setEnabled(!ocupado);
        botaoEntrar.setText(ocupado ? "A entrar..." : "Entrar");
        botaoEntrar.setBackground(ocupado ? CINZA_BORDA : AZUL_PRINCIPAL);
        if (!ocupado) { (campoUsername.getText().isEmpty() ? campoUsername : campoPassword).requestFocusInWindow(); }
    }

    // --- Main para Teste ---
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> { new LoginFrame().setVisible(true); });
    }
}