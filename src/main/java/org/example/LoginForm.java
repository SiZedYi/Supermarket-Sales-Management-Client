package org.example;

import org.example.model.*;
import org.example.rmi.SupermarketService;
import org.example.ui.MainUI;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginForm extends JFrame {
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private SupermarketService service;

    public LoginForm() {
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.1.9", 1099);
            service = (SupermarketService) registry.lookup("SupermarketService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối RMI server");
            return;
        }

        setTitle("Login");
        setSize(300, 200);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("User ID:"));
        txtUserId = new JTextField();
        add(txtUserId);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        add(btnLogin);

        btnLogin.addActionListener(e -> {
            try {
                String userId = txtUserId.getText();
                String password = new String(txtPassword.getPassword());
                Account account = service.login(userId, password);
                if (account != null) {
                    new MainUI(service, userId).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
