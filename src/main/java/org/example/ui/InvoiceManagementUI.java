package org.example.ui;

import org.example.model.Invoice;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class InvoiceManagementUI extends JPanel {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public InvoiceManagementUI(SupermarketService service) {
        this.service = service;
//        setTitle("Manage Invoices");
        setSize(800, 500);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Customer", "Employee", "Order Date"}, 0);
        table = new JTable(tableModel);

        JButton btnViewDetails = new JButton("View Details");
        btnViewDetails.addActionListener(e -> viewInvoiceDetails());

        JPanel panel = new JPanel();
        panel.add(btnViewDetails);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        loadInvoices();
    }

    private void loadInvoices() {
        try {
            tableModel.setRowCount(0);
            List<Invoice> invoices = service.getAllInvoices();
            for (Invoice invoice : invoices) {
                tableModel.addRow(new Object[]{
                        invoice.getInvoiceDetails().get(1),
                        invoice.getCustomer().getContactName(),
                        invoice.getUser().getHoTen(),
                        invoice.getOrderDate()
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoices: " + e.getMessage());
        }
    }

    private void viewInvoiceDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Long invoiceId = (Long) tableModel.getValueAt(selectedRow, 0);
            new InvoiceDetailUI(service, invoiceId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an invoice!");
        }
    }
}
