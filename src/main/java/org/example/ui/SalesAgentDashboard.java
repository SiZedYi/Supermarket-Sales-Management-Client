package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class SalesAgentDashboard extends JFrame {
    public SalesAgentDashboard(SupermarketService service, User user) {
        setTitle("Sales Agent Dashboard");
        setSize(400, 300);
        setLayout(new GridLayout(4, 1));

        JButton btnViewInfo = new JButton("Xem thông tin cá nhân");
        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        JButton btnPayInvoice = new JButton("Thanh toán hóa đơn");

        btnViewInfo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, user.toString());
        });

        btnChangePassword.addActionListener(e -> {
            String newPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
            try {
                service.changePassword(user.getUserId(), newPass);
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnPayInvoice.addActionListener(e -> {
            try {
                String customerId = JOptionPane.showInputDialog(this, "Customer ID:");
                service.payInvoice(Long.valueOf(customerId), Long.valueOf(user.getUserId()), new Date());
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(btnViewInfo);
        add(btnChangePassword);
        add(btnPayInvoice);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
