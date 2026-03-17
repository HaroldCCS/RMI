package com.bank.client;

import com.bank.BankService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Pantalla 3 – Consultar Saldo.
 * Muestra el saldo actual obtenido via RMI al abrirse.
 */
public class BalanceScreen extends JFrame {

    private final BankService bankService;
    private final String      username;

    public BalanceScreen(BankService bankService, String username) {
        this.bankService = bankService;
        this.username    = username;
        buildUI();
        loadBalance(); // Consultar saldo al abrir la ventana
    }

    private JLabel balanceLabel;

    private void buildUI() {
        setTitle("Banco RMI – Consultar Saldo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 45, 30),
                        0, getHeight(), new Color(5, 100, 70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel icon = new JLabel("💰", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        icon.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Saldo Disponible", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(180, 240, 200));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(icon);
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Balance card
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        balanceLabel = new JLabel("Cargando...", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        balanceLabel.setForeground(Color.WHITE);
        card.add(balanceLabel);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(20, 0, 20, 0));
        cardWrapper.add(card, BorderLayout.CENTER);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        // Botones
        JButton refreshBtn = createButton("🔄  Actualizar", new Color(0, 130, 80));
        JButton backBtn    = createButton("← Volver",       new Color(50, 70, 120));

        refreshBtn.addActionListener(e -> loadBalance());
        backBtn.addActionListener(e -> {
            dispose();
            new MenuScreen(bankService, username).setVisible(true);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(backBtn);
        btnPanel.add(refreshBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBalance() {
        balanceLabel.setText("Cargando...");
        new SwingWorker<Double, Void>() {
            Exception error;
            @Override protected Double doInBackground() {
                try { return bankService.getBalance(username); }
                catch (Exception e) { error = e; return null; }
            }
            @Override protected void done() {
                try {
                    Double bal = get();
                    if (bal != null) {
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
                        balanceLabel.setText(fmt.format(bal));
                    } else {
                        balanceLabel.setText("Error");
                        JOptionPane.showMessageDialog(BalanceScreen.this,
                                "Error al obtener saldo: " + (error != null ? error.getMessage() : ""),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) { balanceLabel.setText("Error"); }
            }
        }.execute();
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        Color hover = color.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }
}
