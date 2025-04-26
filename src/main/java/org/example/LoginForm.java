package org.example;

import org.example.model.*;
import org.example.rmi.SupermarketService;
import org.example.ui.ManagerDashboard;
import org.example.ui.SalesAgentDashboard;

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
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
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
                    User user = service.viewUserInfo(userId);
                    if (userId.startsWith("SA")) {
                        new SalesAgentDashboard(service, user).setVisible(true);
                    } else {
                        new ManagerDashboard(service, userId).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thất bại!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
