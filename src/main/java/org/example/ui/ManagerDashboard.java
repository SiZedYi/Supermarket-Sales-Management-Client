package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
public class ManagerDashboard extends JFrame {
    private SupermarketService service;
    private String userId;

    public ManagerDashboard(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;

        setTitle("Manager Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton btnViewInfo = new JButton("Xem thông tin cá nhân");
        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        JButton btnManageCategories = new JButton("Quản lý loại hàng");
        JButton btnManageProducts = new JButton("Quản lý sản phẩm");
        JButton btnManageInvoices = new JButton("Quản lý hóa đơn");
        JButton btnManageEmployees = new JButton("Quản lý người dùng");

        btnViewInfo.addActionListener(e -> viewInfo());
        btnChangePassword.addActionListener(e -> changePassword());
        btnManageCategories.addActionListener(e -> manageCategories());
        btnManageProducts.addActionListener(e -> new ProductListUI(service).setVisible(true));
        btnManageInvoices.addActionListener(e -> new PayInvoiceUI(service).setVisible(true));
        btnManageEmployees.addActionListener(e -> new EmployeeManagementUI(service).setVisible(true));

        panel.add(btnViewInfo);
        panel.add(btnChangePassword);
        panel.add(btnManageCategories);
        panel.add(btnManageProducts);
        panel.add(btnManageInvoices);
        panel.add(btnManageEmployees);

        add(panel);
    }

    private void viewInfo() {
        try {
            User user = service.viewUserInfo(userId);
            JOptionPane.showMessageDialog(this, "Tên: " + user.getHoTen() + "\nEmail: " + user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePassword() {
        String newPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
        if (newPass != null && !newPass.trim().isEmpty()) {
            try {
                service.changePassword(userId, newPass);
                JOptionPane.showMessageDialog(this, "Đã đổi mật khẩu thành công!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void manageCategories() {
        JOptionPane.showMessageDialog(this, "Tính năng này đang được phát triển.");
    }

    private void manageInvoices() {
        JOptionPane.showMessageDialog(this, "Tính năng này đang được phát triển.");
    }
}
