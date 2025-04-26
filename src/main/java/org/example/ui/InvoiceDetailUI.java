package org.example.ui;

import org.example.model.InvoiceDetail;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class InvoiceDetailUI extends JFrame {
    private SupermarketService service;
    private Long invoiceId;
    private JTable table;
    private DefaultTableModel tableModel;

    public InvoiceDetailUI(SupermarketService service, Long invoiceId) {
        this.service = service;
        this.invoiceId = invoiceId;
        setTitle("Invoice Details");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"Detail ID", "Product Name", "Quantity", "Price"}, 0);
        table = new JTable(tableModel);

        JButton btnRemove = new JButton("Remove Product");
        btnRemove.addActionListener(e -> removeProduct());

        JPanel panel = new JPanel();
        panel.add(btnRemove);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        loadInvoiceDetails();
    }

    private void loadInvoiceDetails() {
        try {
            tableModel.setRowCount(0);
            List<InvoiceDetail> details = service.listInvoiceDetails(invoiceId); // Cần thêm service listInvoiceDetails(invoiceId)
            for (InvoiceDetail detail : details) {
                tableModel.addRow(new Object[]{
                        detail.getId(),
                        detail.getProduct().getProductName(),
                        detail.getQuantity(),
                        detail.getUnitPrice()
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoice details: " + e.getMessage());
        }
    }

    private void removeProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Long detailId = (Long) tableModel.getValueAt(selectedRow, 0);
            try {
                service.cancelInvoiceDetail(detailId);
                JOptionPane.showMessageDialog(this, "Product removed successfully!");
                loadInvoiceDetails(); // Reload sau khi xóa
            } catch (RemoteException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to remove product: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to remove!");
        }
    }
}
