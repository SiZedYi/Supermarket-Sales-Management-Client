package org.example.ui;

import org.example.rmi.SupermarketService;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Date;

public class PayInvoiceUI extends JFrame {
    private SupermarketService service;
    private JTextField txtCustomerId;
    private JTextField txtEmployeeId;
    private JButton btnPay;

    public PayInvoiceUI(SupermarketService service) {
        this.service = service;
        setTitle("Pay Invoice");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Customer ID:"));
        txtCustomerId = new JTextField();
        add(txtCustomerId);

        add(new JLabel("Employee ID:"));
        txtEmployeeId = new JTextField();
        add(txtEmployeeId);

        btnPay = new JButton("Pay Invoice");
        add(new JLabel()); // empty label
        add(btnPay);

        btnPay.addActionListener(e -> payInvoice());
    }

    private void payInvoice() {
        try {
            String customerId = txtCustomerId.getText();
            String employeeId = txtEmployeeId.getText();
            service.payInvoice(customerId, employeeId, new Date());
            JOptionPane.showMessageDialog(this, "Invoice paid successfully!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to pay invoice: " + e.getMessage());
        }
    }
}
