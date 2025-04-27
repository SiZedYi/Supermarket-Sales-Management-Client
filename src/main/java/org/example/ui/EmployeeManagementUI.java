package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.util.List;
import java.awt.*;
import java.rmi.RemoteException;

public class EmployeeManagementUI extends JPanel {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public EmployeeManagementUI(SupermarketService service) {
        this.service = service;
//        setTitle("Employee Management");
        setSize(800, 500);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"UserId", "Full Name", "Phone", "Email", "Role"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton addButton = new JButton("Add User");
        JButton deleteButton = new JButton("Delete User");
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

    public void loadEmployees() {
        try {
            List<User> users = service.listEmployees();
            tableModel.setRowCount(0);
            for (User emp : users) {
                tableModel.addRow(new Object[]{
                        emp.getUserId(),
                        emp.getHoTen(),
                        emp.getCccd(),
                        emp.getEmail(),
                        emp.getUserId().startsWith("SA") ? "Sales Agent" : "User",
                });
            }

            // Ẩn cột userId trong JTable (cột đầu tiên)
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);
            table.getColumnModel().getColumn(0).setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        AddUserUI addUserUI = new AddUserUI(service, this); // this = parent frame, true = modal
        addUserUI.setVisible(true);
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