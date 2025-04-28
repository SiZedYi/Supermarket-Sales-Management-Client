package org.example;

import org.example.model.*;
import org.example.rmi.SupermarketService;
import org.example.ui.MainUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginForm extends JFrame {
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private SupermarketService service;
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JButton btnLogin;
    private Color primaryColor = new Color(70, 130, 180); // Steel blue
    private Color secondaryColor = new Color(240, 248, 255); // Alice blue

    public LoginForm() {
        initializeConnection();
        setupUI();
        addActionListeners();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (SupermarketService) registry.lookup("SupermarketService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối RMI server",
                    "Lỗi Kết Nối",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupUI() {
        setTitle("Supermarket Login");
        setSize(450, 400);

        // Gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, primaryColor, 0, getHeight(), secondaryColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SUPERMARKET SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        ImageIcon icon = createDefaultIcon();
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(iconLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // User ID field
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        txtUserId = createStyledTextField();
        formPanel.add(txtUserId, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        txtPassword = createStyledPasswordField();
        formPanel.add(txtPassword, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        btnLogin = createStyledButton("Login");
        btnLogin.setIcon(new ImageIcon("path/to/login-icon.png")); // Add icon path
        buttonPanel.add(btnLogin);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private ImageIcon createDefaultIcon() {
        // Create a simple icon if you don't have a real one
        int size = 64;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Draw a simple icon
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(primaryColor);
        g2d.fillOval(4, 4, size-8, size-8);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(16, 16, size-32, size-32);
        g2d.setColor(primaryColor);
        g2d.fillRect(size/2-3, 16, 6, size/2);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add focus effect
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor, 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });

        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add focus effect
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor, 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });

        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private void addActionListeners() {
        btnLogin.addActionListener(e -> {
            btnLogin.setEnabled(false);
            statusLabel.setText("Đang đăng nhập...");

            // Use SwingWorker to handle login in background
            SwingWorker<Account, Void> worker = new SwingWorker<Account, Void>() {
                @Override
                protected Account doInBackground() throws Exception {
                    String userId = txtUserId.getText();
                    String password = new String(txtPassword.getPassword());
                    return service.login(userId, password);
                }

                @Override
                protected void done() {
                    try {
                        Account account = get();
                        if (account != null) {
                            new MainUI(service, txtUserId.getText()).setVisible(true);
                            dispose();
                        } else {
                            statusLabel.setText("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                            txtPassword.setText("");
                            btnLogin.setEnabled(true);
                            shakeComponent(mainPanel);
                        }
                    } catch (Exception ex) {
                        statusLabel.setText("Lỗi: " + ex.getMessage());
                        btnLogin.setEnabled(true);
                    }
                }
            };

            worker.execute();
        });

        // Add key listener to enable login with Enter key
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });
    }

    // Animation effect for failed login
    private void shakeComponent(Component component) {
        final int originalX = component.getLocation().x;
        final int maxDistance = 10;

        Timer timer = new Timer(20, null);
        final int[] steps = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final int[] passes = {1, 2, 3};

        timer.addActionListener(new java.awt.event.ActionListener() {
            int currentStep = 0;
            int currentPass = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (currentPass >= passes.length) {
                    timer.stop();
                    component.setLocation(originalX, component.getLocation().y);
                    return;
                }

                int direction = currentStep % 2 == 0 ? 1 : -1;
                int distance = maxDistance - (currentStep / 2);
                component.setLocation(originalX + (direction * distance), component.getLocation().y);

                currentStep++;
                if (currentStep >= steps.length) {
                    currentStep = 0;
                    currentPass++;
                }
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Custom color scheme
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}