package com.bank.client;

import com.bank.BankService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla 2 – Menú principal.
 * Muestra dos opciones: Consultar Saldo y Realizar Retiro.
 */
public class MenuScreen extends JFrame {

    private final BankService bankService;
    private final String      username;

    public MenuScreen(BankService bankService, String username) {
        this.bankService = bankService;
        this.username    = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("Banco RMI – Menú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 370);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 25, 55),
                        0, getHeight(), new Color(8, 70, 115));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Saludo
        JLabel welcome = new JLabel("Bienvenido, " + username + " 👋", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setForeground(Color.WHITE);
        welcome.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel subtitle = new JLabel("¿Qué deseas hacer hoy?", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(160, 200, 240));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(welcome);
        headerPanel.add(subtitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Botones de opciones
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(30, 0, 20, 0));

        JButton balanceBtn  = createMenuButton("💰  Consultar Saldo",  new Color(0, 150, 100));
        JButton withdrawBtn = createMenuButton("💸  Realizar Retiro",  new Color(180, 80, 0));

        balanceBtn.addActionListener(e -> {
            dispose();
            new BalanceScreen(bankService, username).setVisible(true);
        });

        withdrawBtn.addActionListener(e -> {
            dispose();
            new WithdrawScreen(bankService, username).setVisible(true);
        });

        optionsPanel.add(balanceBtn);
        optionsPanel.add(withdrawBtn);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        // Cerrar sesión
        JButton logoutBtn = new JButton("Cerrar sesión");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(160, 200, 240));
        logoutBtn.setBackground(new Color(20, 40, 80));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen(bankService).setVisible(true);
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(logoutBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 55));
        Color hoverColor = color.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }
}
