package org.example.ui;

import org.example.model.User;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

public class AccountInfoUI extends JPanel {
    private SupermarketService service;
    private String userId;
    private User currentUser;

    // UI Components
    private JTextField tfName;
    private JTextField tfBirthDate;
    private JTextField tfGender;
    private JTextField tfEmail;
    private JTextField tfCCCD;
    private JButton btnChangePassword;
//    private JButton btnEditInfo;

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Lighter blue
    private final Color ACCENT_COLOR = new Color(231, 76, 60); // Red accent
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light gray
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Dark blue-gray
    private final Color FIELD_BG_COLOR = new Color(250, 250, 250); // Almost white

    // Fonts
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public AccountInfoUI(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create main container with some padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainContainer.setBackground(BACKGROUND_COLOR);

        // Add header panel
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Create user info card
        JPanel userInfoCard = createUserInfoCard();
        mainContainer.add(userInfoCard, BorderLayout.CENTER);

        // Create action buttons panel
        JPanel buttonPanel = createButtonPanel();
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);

        // Load user information
        loadUserInfo();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // User icon
        JLabel iconLabel = new JLabel(createUserIcon(60, 60));
        panel.add(iconLabel, BorderLayout.WEST);

        // Title and subtitle
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Account Information");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("View and manage your personal information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 150, 150));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        panel.add(titlePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserInfoCard() {
        // Main card panel with shadow effect
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(createShadowBorder());

        // Form panel for user details
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Create form fields
        tfName = createStyledTextField();
        tfBirthDate = createStyledTextField();
        tfGender = createStyledTextField();
        tfEmail = createStyledTextField();
        tfCCCD = createStyledTextField();

        // Add components to the form with GridBagLayout
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = 0;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(10, 5, 10, 15);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = 0;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(10, 0, 10, 5);

        // Add Full Name
        formPanel.add(createStyledLabel("Full Name:"), labelGbc);
        labelGbc.gridy++;
        formPanel.add(tfName, fieldGbc);
        fieldGbc.gridy++;

        // Add Birth Date
        formPanel.add(createStyledLabel("Birth Date:"), labelGbc);
        labelGbc.gridy++;
        formPanel.add(tfBirthDate, fieldGbc);
        fieldGbc.gridy++;

        // Add Gender
        formPanel.add(createStyledLabel("Gender:"), labelGbc);
        labelGbc.gridy++;
        formPanel.add(tfGender, fieldGbc);
        fieldGbc.gridy++;

        // Add Email
        formPanel.add(createStyledLabel("Email:"), labelGbc);
        labelGbc.gridy++;
        formPanel.add(tfEmail, fieldGbc);
        fieldGbc.gridy++;

        // Add CCCD
        formPanel.add(createStyledLabel("CCCD:"), labelGbc);
        labelGbc.gridy++;
        formPanel.add(tfCCCD, fieldGbc);
        fieldGbc.gridy++;

        cardPanel.add(formPanel, BorderLayout.CENTER);

        return cardPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Edit Info Button
//        btnEditInfo = createStyledButton("Edit Information", SECONDARY_COLOR);
//        btnEditInfo.setIcon(createIcon("✎", 16, Color.WHITE)); // Edit icon
//        btnEditInfo.addActionListener(e -> handleEditInfo());

        // Change Password Button
        btnChangePassword = createStyledButton("Change Password", PRIMARY_COLOR);
        btnChangePassword.setIcon(createIcon("🔒", 16, Color.WHITE)); // Lock icon
        btnChangePassword.addActionListener(e -> handleChangePassword());

//        panel.add(btnEditInfo);
        panel.add(btnChangePassword);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 45));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darken(bgColor, 0.9f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(darken(bgColor, 0.8f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(darken(bgColor, 0.9f));
                }
            }
        });

        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setEditable(false);
        field.setBackground(FIELD_BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private Border createShadowBorder() {
        // Create a compound border with a rounded line border and shadow effect
        Border line = BorderFactory.createLineBorder(new Color(230, 230, 230), 1);
        Border empty = new EmptyBorder(5, 5, 5, 5);
        Border matteBorder = BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(240, 240, 240));

        return BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(matteBorder, line),
                empty
        );
    }

    private Color darken(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0),
                color.getAlpha()
        );
    }

    private ImageIcon createUserIcon(int width, int height) {
        // Create a simple user icon
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background circle
        g2.setColor(PRIMARY_COLOR);
        g2.fillOval(0, 0, width, height);

        // Draw head
        g2.setColor(Color.WHITE);
        g2.fillOval(width/4, height/8, width/2, width/2);

        // Draw body
        g2.fillArc(width/6, height/2, width*2/3, height, 0, 180);

        g2.dispose();
        return new ImageIcon(image);
    }

    private Icon createIcon(String text, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("Dialog", Font.BOLD, size));
                g2d.setColor(color);
                g2d.drawString(text, x, y + size);
                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    private void loadUserInfo() {
        try {
            currentUser = service.viewUserInfo(userId);
            if (currentUser != null) {
                tfName.setText(currentUser.getHoTen());
                if (currentUser.getNgaySinh() != null) {
                    tfBirthDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(currentUser.getNgaySinh()));
                }
                tfGender.setText(currentUser.getGioiTinh());
                tfEmail.setText(currentUser.getEmail());
                tfCCCD.setText(currentUser.getCccd());
            } else {
                showErrorDialog("User not found", "Could not find user information in the system.");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            showErrorDialog("Connection Error", "Failed to load user information from the server.");
        }
    }

    private void handleEditInfo() {
        // This function will be implemented when needed
        // Toggle editable state or create a separate dialog for editing
        JOptionPane.showMessageDialog(
                this,
                "This feature is coming soon!",
                "Edit Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleChangePassword() {
        // Create custom password change panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Title
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Password fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JPasswordField currentPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();
        stylePasswordField(currentPassField);
        stylePasswordField(newPassField);

        fieldsPanel.add(new JLabel("Current Password:"));
        fieldsPanel.add(currentPassField);
        fieldsPanel.add(new JLabel("New Password:"));
        fieldsPanel.add(newPassField);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Show custom dialog
        int result = JOptionPane.showConfirmDialog(
                this, mainPanel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());

            // Validate passwords
            if (newPass.isEmpty()) {
                showErrorDialog("Invalid Password", "New password cannot be empty.");
                return;
            }

            try {
                // In a real implementation, you would verify the current password first
                service.changePassword(userId, newPass);
                showSuccessDialog("Success", "Your password has been changed successfully!");
            } catch (RemoteException ex) {
                ex.printStackTrace();
                showErrorDialog("Error", "Failed to change password. Please try again.");
            }
        }
    }


    private void stylePasswordField(JPasswordField field) {
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void showSuccessDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, PRIMARY_COLOR);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, ACCENT_COLOR);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createDialogPanel(String message, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create color stripe on the left
        JPanel colorStripe = new JPanel();
        colorStripe.setBackground(accentColor);
        colorStripe.setPreferredSize(new Dimension(5, 0));

        // Message with proper font
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(FIELD_FONT);

        panel.add(colorStripe, BorderLayout.WEST);
        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }
}