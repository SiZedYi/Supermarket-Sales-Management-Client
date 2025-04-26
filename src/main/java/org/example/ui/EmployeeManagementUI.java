package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.util.List;
import java.awt.*;
import java.rmi.RemoteException;

public class EmployeeManagementUI extends JFrame {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public EmployeeManagementUI(SupermarketService service) {
        this.service = service;
        setTitle("Employee Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"User ID", "Full Name", "Phone", "Email"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton addButton = new JButton("Add Employee");
        JButton deleteButton = new JButton("Delete Employee");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        loadEmployees();

        addButton.addActionListener(e -> addEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = service.listEmployees();
            tableModel.setRowCount(0);
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                        emp.getUserId(),
                        emp.getUser().getHoTen(),
                        emp.getUser().getCccd(),
                        emp.getUser().getEmail()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        JTextField userIdField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();

        Object[] fields = {
                "User ID:", userIdField,
                "Full Name:", fullNameField,
                "Phone:", phoneField,
                "Email:", emailField,
                "Password:", passwordField,
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                User user = new User();
                user.setUserId(userIdField.getText());
                user.setHoTen(fullNameField.getText());
                user.setCccd(phoneField.getText());
                user.setEmail(emailField.getText());

                Employee employee = new Employee();
                employee.setUser(user);

                service.addEmployee(employee);
                service.changePassword(user.getUserId(), passwordField.getText());
                JOptionPane.showMessageDialog(this, "Employee added successfully!");

                loadEmployees();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String userId = tableModel.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete employee " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    service.deleteEmployee(userId);
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    loadEmployees();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!");
        }
    }
}