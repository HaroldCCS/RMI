package com.bank.client;

import com.bank.BankService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla 1 – Login.
 * El usuario ingresa username y contraseña. Se llama BankService.login() via RMI.
 */
public class LoginScreen extends JFrame {

    private final BankService bankService;

    public LoginScreen(BankService bankService) {
        this.bankService = bankService;
        buildUI();
    }

    private void buildUI() {
        setTitle("Banco RMI – Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo degradado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 60),
                        0, getHeight(), new Color(10, 80, 130));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Título
        JLabel title = new JLabel("🏦  Banco RMI", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        formPanel.setOpaque(false);

        JTextField userField = createTextField("Usuario");
        JPasswordField passField = new JPasswordField();
        passField.setToolTipText("Contraseña");
        styleField(passField);
        passField.setEchoChar('●');

        JLabel userLabel = createLabel("Usuario");
        JLabel passLabel = createLabel("Contraseña");

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botón
        JButton loginBtn = createButton("Ingresar");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // Acción login
        ActionListener doLogin = e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                showError("Ingrese usuario y contraseña.");
                return;
            }
            loginBtn.setEnabled(false);
            loginBtn.setText("Verificando...");
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                Exception error;
                @Override protected Boolean doInBackground() throws Exception {
                    try { return bankService.login(username, password); }
                    catch (Exception ex) { error = ex; return false; }
                }
                @Override protected void done() {
                    try {
                        if (get()) {
                            dispose();
                            new MenuScreen(bankService, username).setVisible(true);
                        } else if (error != null) {
                            showError("Error de conexión: " + error.getMessage());
                        } else {
                            showError("Usuario o contraseña incorrectos.");
                        }
                    } catch (Exception ex) { showError(ex.getMessage()); }
                    finally { loginBtn.setEnabled(true); loginBtn.setText("Ingresar"); }
                }
            };
            worker.execute();
        };

        loginBtn.addActionListener(doLogin);
        passField.addActionListener(doLogin); // Enter desde el campo contraseña

        add(mainPanel);
    }

    // ── Helpers de estilo ──────────────────────────────────────────────────────

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 210, 240));
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(30, 50, 90));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 130, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 150, 255)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(0, 120, 215)); }
        });
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
