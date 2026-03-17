package com.bank.client;

import com.bank.BankService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Pantalla 4 – Realizar Retiro.
 * El usuario ingresa el monto a retirar y presiona el botón Retirar.
 */
public class WithdrawScreen extends JFrame {

    private final BankService bankService;
    private final String      username;

    public WithdrawScreen(BankService bankService, String username) {
        this.bankService = bankService;
        this.username    = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("Banco RMI – Realizar Retiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 15, 10),
                        0, getHeight(), new Color(130, 50, 10));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Header
        JLabel icon = new JLabel("💸", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));

        JLabel title = new JLabel("Realizar Retiro", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(255, 210, 150));

        JLabel subTxt = new JLabel("Usuario: " + username, SwingConstants.CENTER);
        subTxt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subTxt.setForeground(new Color(220, 180, 130));
        subTxt.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(icon);
        headerPanel.add(title);
        headerPanel.add(subTxt);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(25, 0, 20, 0));

        JLabel amtLabel = new JLabel("Monto a retirar ($)");
        amtLabel.setForeground(new Color(255, 200, 130));
        amtLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextField amtField = new JTextField();
        amtField.setBackground(new Color(70, 30, 10));
        amtField.setForeground(Color.WHITE);
        amtField.setCaretColor(Color.WHITE);
        amtField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        amtField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 120, 50), 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultLabel.setForeground(Color.WHITE);

        formPanel.add(amtLabel);
        formPanel.add(amtField);
        formPanel.add(resultLabel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botones
        JButton withdrawBtn = createButton("💸  Retirar",   new Color(200, 80, 0));
        JButton backBtn     = createButton("← Volver",     new Color(60, 40, 70));

        withdrawBtn.addActionListener(e -> {
            String raw = amtField.getText().trim().replace(",", ".");
            if (raw.isEmpty()) {
                showError("Ingrese un monto.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(raw);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showError("Ingrese un monto válido mayor a 0.");
                return;
            }
            withdrawBtn.setEnabled(false);
            withdrawBtn.setText("Procesando...");
            resultLabel.setForeground(Color.WHITE);
            resultLabel.setText("Procesando retiro...");

            final double finalAmount = amount;
            new SwingWorker<Boolean, Void>() {
                Exception error;
                @Override protected Boolean doInBackground() {
                    try { return bankService.withdraw(username, finalAmount); }
                    catch (Exception ex) { error = ex; return null; }
                }
                @Override protected void done() {
                    try {
                        Boolean ok = get();
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
                        if (ok == null) {
                            resultLabel.setForeground(new Color(255, 100, 80));
                            resultLabel.setText("Error: " + (error != null ? error.getMessage() : "desconocido"));
                        } else if (ok) {
                            resultLabel.setForeground(new Color(100, 255, 150));
                            resultLabel.setText("✓ Retiro exitoso de " + fmt.format(finalAmount));
                            amtField.setText("");
                        } else {
                            resultLabel.setForeground(new Color(255, 180, 50));
                            resultLabel.setText("✗ Saldo insuficiente para " + fmt.format(finalAmount));
                        }
                    } catch (Exception ex) {
                        resultLabel.setText("Error inesperado.");
                    } finally {
                        withdrawBtn.setEnabled(true);
                        withdrawBtn.setText("💸  Retirar");
                    }
                }
            }.execute();
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MenuScreen(bankService, username).setVisible(true);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(backBtn);
        btnPanel.add(withdrawBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
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

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
