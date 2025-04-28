package org.example.ui;

import org.example.model.User;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

public class AccountInfoUI extends JPanel {
    private SupermarketService service;
    private String userId;
    private User currentUser;

    private JTextField tfName;
    private JTextField tfBirthDate;
    private JTextField tfGender;
    private JTextField tfEmail;
    private JTextField tfCCCD;
    private JButton btnChangePassword;

    public AccountInfoUI(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;

        setLayout(new BorderLayout());
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(container, BorderLayout.CENTER);

        // Form panel in center
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        formPanel.add(new JLabel("Full Name:"));
        tfName = new JTextField(); tfName.setEditable(false);
        formPanel.add(tfName);

        formPanel.add(new JLabel("Birth Date:"));
        tfBirthDate = new JTextField(); tfBirthDate.setEditable(false);
        formPanel.add(tfBirthDate);

        formPanel.add(new JLabel("Gender:"));
        tfGender = new JTextField(); tfGender.setEditable(false);
        formPanel.add(tfGender);

        formPanel.add(new JLabel("Email:"));
        tfEmail = new JTextField(); tfEmail.setEditable(false);
        formPanel.add(tfEmail);

        formPanel.add(new JLabel("CCCD:"));
        tfCCCD = new JTextField(); tfCCCD.setEditable(false);
        formPanel.add(tfCCCD);

        container.add(formPanel, BorderLayout.CENTER);

        // Button panel at bottom
        JPanel btnPanel = new JPanel();
        btnChangePassword = new JButton("Change Password");
        btnChangePassword.setFont(new Font("Arial", Font.BOLD, 14));
        btnChangePassword.setBackground(new Color(0, 120, 215));
        btnChangePassword.setForeground(Color.WHITE);
        btnChangePassword.setFocusPainted(false);
        btnChangePassword.addActionListener(e -> handleChangePassword());
        btnPanel.add(btnChangePassword);
        container.add(btnPanel, BorderLayout.SOUTH);

        loadUserInfo();
    }

    private void loadUserInfo() {
        try {
            currentUser = service.viewUserInfo(userId);
            if (currentUser != null) {
                tfName.setText(currentUser.getHoTen());
                if (currentUser.getNgaySinh() != null) {
                    tfBirthDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(currentUser.getNgaySinh()));
                }
                tfGender.setText(currentUser.getGioiTinh());
                tfEmail.setText(currentUser.getEmail());
                tfCCCD.setText(currentUser.getCccd());
            } else {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load user info.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChangePassword() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        JPasswordField newPassField = new JPasswordField();
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newPass = new String(newPassField.getPassword());
            try {
                service.changePassword(userId, newPass);
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error changing password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
