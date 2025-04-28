package org.example.ui;

import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Arrays;

public class AddUserUI extends JFrame {
    private SupermarketService service;
    private JTextField txtHoTen, txtEmail;
    private JFormattedTextField txtNgaySinh, txtPhone;
    private JPasswordField txtPassword;
    private JComboBox<String> cbGioiTinh, cbRole;
    private EmployeeManagementUI employeeManagementUI;
    private JButton btnSave, btnCancel;
    private JPanel formPanel, buttonPanel;

    // Colors
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel blue
    private final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private final Color BUTTON_COLOR = new Color(70, 130, 180);
    private final Color CANCEL_COLOR = new Color(190, 190, 190);

    public AddUserUI(SupermarketService service, EmployeeManagementUI employeeManagementUI) {
        this.service = service;
        this.employeeManagementUI = employeeManagementUI;

        setupUI();
        initComponents();
        addComponents();
        addEventListeners();

        // Display the form
        setVisible(true);
    }

    private void setupUI() {
        setTitle("Thêm Nhân Viên Mới");
        setSize(700, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setResizable(false);

        // Add some padding to the frame
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void initComponents() {
        // Create form panel with GridBagLayout for more control
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Header
        JLabel headerLabel = new JLabel("Thông Tin Nhân Viên");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);

        // Form fields
        txtHoTen = createStyledTextField();

        // Formatted date field
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            txtNgaySinh = new JFormattedTextField(dateFormatter);
            styleFormattedField(txtNgaySinh);

            // Set current date as default
            Calendar cal = Calendar.getInstance();
            String defaultDate = String.format("%04d-%02d-%02d",
                    cal.get(Calendar.YEAR) - 20, // Default age of 20
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            txtNgaySinh.setValue(defaultDate);
        } catch (ParseException e) {
            txtNgaySinh = new JFormattedTextField();
            styleFormattedField(txtNgaySinh);
        }

        // Phone field with format
        try {
            MaskFormatter phoneFormatter = new MaskFormatter("##########");
            txtPhone = new JFormattedTextField(phoneFormatter);
            styleFormattedField(txtPhone);
        } catch (ParseException e) {
            txtPhone = new JFormattedTextField();
            styleFormattedField(txtPhone);
        }

        // Gender dropdown
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        styleComboBox(cbGioiTinh);

        txtEmail = createStyledTextField();

        txtPassword = new JPasswordField();
        stylePasswordField(txtPassword);

        cbRole = new JComboBox<>(new String[]{"User", "SalesAgent"});
        styleComboBox(cbRole);

        // Buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnCancel = new JButton("Hủy");
        styleButton(btnCancel, CANCEL_COLOR, Color.DARK_GRAY);

        btnSave = new JButton("Lưu");
        styleButton(btnSave, BUTTON_COLOR, Color.WHITE);
    }

    private void addComponents() {
        // Set up GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        // Add header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 20, 5);
        formPanel.add(createHeaderPanel(), gbc);

        // Reset insets for form fields
        gbc.insets = new Insets(8, 5, 8, 5);

        // Add form fields with labels
        addFormField(formPanel, "Họ và tên:", txtHoTen, "Nhập họ tên đầy đủ", 1, gbc);
        addFormField(formPanel, "Ngày sinh (YYYY-MM-DD):", txtNgaySinh, "", 2, gbc);
        addFormField(formPanel, "Số điện thoại:", txtPhone, "Nhập số điện thoại", 3, gbc);
        addFormField(formPanel, "Giới tính:", cbGioiTinh, "", 4, gbc);
        addFormField(formPanel, "Email:", txtEmail, "Nhập địa chỉ email", 5, gbc);
        addFormField(formPanel, "Mật khẩu:", txtPassword, "Nhập mật khẩu", 6, gbc);
        addFormField(formPanel, "Vai trò:", cbRole, "", 7, gbc);

        // Add buttons to button panel
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        // Add button panel to form panel
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        // Add form panel to frame
        add(formPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        // Create icon
        JLabel iconLabel = new JLabel(createUserIcon());

        // Create title
        JLabel titleLabel = new JLabel("Thêm Nhân Viên Mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private ImageIcon createUserIcon() {
        int size = 48;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circle background
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, size, size);

        // Draw user icon
        g2d.setColor(Color.WHITE);
        // Head
        g2d.fillOval(size/4, size/6, size/2, size/2);
        // Body
        g2d.fillArc(size/8, size/2, size*3/4, size, 0, 180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, String placeholder, int row, GridBagConstraints gbc) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);

        // Field
        gbc.gridx = 1;
        gbc.weightx = 0.7;

        // Add placeholder if applicable and field is a text field
        if (!placeholder.isEmpty() && field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            addPlaceholder(textField, placeholder);
        }

        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Add focus effect
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        return textField;
    }

    private void styleFormattedField(JFormattedTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Add focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Add focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 35));
        comboBox.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        comboBox.setBackground(Color.WHITE);

        // Customize the appearance
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                ((JComponent) c).setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void addEventListeners() {
        // Save button action
        btnSave.addActionListener(this::saveUser);

        // Cancel button action
        btnCancel.addActionListener(e -> dispose());

        // Add key listener to enable save with Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.doClick();
                }
            }
        };

        txtHoTen.addKeyListener(enterKeyListener);
        txtNgaySinh.addKeyListener(enterKeyListener);
        txtPhone.addKeyListener(enterKeyListener);
        txtEmail.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);
    }

    private void saveUser(ActionEvent e) {
        try {
            // Validate input fields
            if (!validateInputs()) {
                return;
            }

            String hoTen = txtHoTen.getText();

            // Chuyển đổi chuỗi ngày tháng sang kiểu Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngaySinh = null;
            try {
                ngaySinh = dateFormat.parse(txtNgaySinh.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Ngày sinh không đúng định dạng! Vui lòng nhập theo định dạng YYYY-MM-DD",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return;
            }

            String phone = txtPhone.getText();
            String gioiTinh = (String) cbGioiTinh.getSelectedItem();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String role = (String) cbRole.getSelectedItem();

            // Tạo ID mới
            String prefix = "User".equalsIgnoreCase(role) ? "U" : "SA";
            String id = prefix + phone;

            // Tạo user với constructor mới
            User user = new User(id, hoTen, ngaySinh, phone, gioiTinh, email);

            // Hiển thị dialog xác nhận
            showConfirmationDialog(user, password);

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        // Validate họ tên
        if (txtHoTen.getText().isEmpty() || txtHoTen.getText().equals("Nhập họ tên đầy đủ")) {
            showValidationError("Vui lòng nhập họ tên");
            txtHoTen.requestFocus();
            return false;
        }

        // Validate phone
        if (txtPhone.getText().replaceAll("_", "").length() < 10) {
            showValidationError("Số điện thoại phải có 10 chữ số");
            txtPhone.requestFocus();
            return false;
        }

        // Validate email
        String email = txtEmail.getText();
        if (email.isEmpty() || email.equals("Nhập địa chỉ email") || !email.contains("@") || !email.contains(".")) {
            showValidationError("Vui lòng nhập email hợp lệ");
            txtEmail.requestFocus();
            return false;
        }

        // Validate password
        if (txtPassword.getPassword().length < 4) {
            showValidationError("Mật khẩu phải có ít nhất 4 ký tự");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void showConfirmationDialog(User user, String password) {
        // Create formatted information for confirmation
        String confirmMessage = "<html><body>"
                + "<h2 style='color:#4682B4;margin-bottom:10px;'>Xác nhận thông tin nhân viên</h2>"
                + "<table style='width:100%'>"
                + "<tr><td style='font-weight:bold;padding:3px'>ID:</td><td>" + user.getUserId() + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Họ tên:</td><td>" + user.getHoTen() + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Ngày sinh:</td><td>" + new SimpleDateFormat("dd/MM/yyyy").format(user.getNgaySinh()) + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Số điện thoại:</td><td>" + user.getCccd() + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Giới tính:</td><td>" + user.getGioiTinh() + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Email:</td><td>" + user.getEmail() + "</td></tr>"
                + "<tr><td style='font-weight:bold;padding:3px'>Vai trò:</td><td>" + (user.getUserId().startsWith("U") ? "User" : "SalesAgent") + "</td></tr>"
                + "</table>"
                + "<p style='margin-top:15px'>Bạn có chắc chắn muốn thêm nhân viên này?</p>"
                + "</body></html>";

        int option = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Xác nhận thông tin",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                // Gọi service với user và password
                service.addUser(user, password);

                JOptionPane.showMessageDialog(this,
                        "Thêm nhân viên thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                if (employeeManagementUI != null) {
                    employeeManagementUI.loadEmployees();
                    dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi thêm nhân viên: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}