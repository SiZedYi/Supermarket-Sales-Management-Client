package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class AddUserUI extends JFrame {
    private SupermarketService service;
    private JTextField txtHoTen, txtNgaySinh, txtCCCD, txtGioiTinh, txtEmail, txtPassword;
    private JComboBox<String> cbRole;
    private EmployeeManagementUI employeeManagementUI;

    public AddUserUI(SupermarketService service, EmployeeManagementUI employeeManagementUI) {
        this.service = service;
        this.employeeManagementUI = employeeManagementUI;
        setTitle("Add User"); setSize(400,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8,2,5,5));

        add(new JLabel("Họ Tên:")); txtHoTen=new JTextField(); add(txtHoTen);
        add(new JLabel("Ngày Sinh (yyyy-MM-dd):")); txtNgaySinh=new JTextField(); add(txtNgaySinh);
        add(new JLabel("CCCD:")); txtCCCD=new JTextField(); add(txtCCCD);
        add(new JLabel("Giới Tính:")); txtGioiTinh=new JTextField(); add(txtGioiTinh);
        add(new JLabel("Email:")); txtEmail=new JTextField(); add(txtEmail);
        add(new JLabel("Password:")); txtPassword=new JTextField(); add(txtPassword);
        add(new JLabel("Role:")); cbRole=new JComboBox<>(new String[]{"User","SalesAgent"}); add(cbRole);

        JButton btnSave=new JButton("Save");
        btnSave.addActionListener(this::saveUser);
        add(new JLabel()); add(btnSave);
    }

    private void saveUser(ActionEvent e) {
        try {
            String hoTen = txtHoTen.getText();
            // Chuyển đổi chuỗi ngày tháng sang kiểu Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngaySinh = null;
            try {
                ngaySinh = dateFormat.parse(txtNgaySinh.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng! Vui lòng nhập theo định dạng dd/MM/yyyy");
                return;
            }
            String cccd = txtCCCD.getText();
            String gioiTinh = txtGioiTinh.getText();
            String email = txtEmail.getText();
            String password = txtPassword.getText();
            String role = (String)cbRole.getSelectedItem();
            // Tạo ID mới
            String prefix = "User".equalsIgnoreCase(role) ? "U" : "SA";
            String id = prefix + cccd;


            // Tạo user với constructor mới
            User user = new User(id, hoTen, ngaySinh, cccd, gioiTinh, email);

            // Gọi service với user và password
            service.addUser(user, password);

            JOptionPane.showMessageDialog(this,"User added successfully");
            if(employeeManagementUI!=null){
                employeeManagementUI.loadEmployees();
                dispose();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }
}
