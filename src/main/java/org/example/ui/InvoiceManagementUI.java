package org.example.ui;

import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.model.User;
import org.example.model.Customer;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;

public class InvoiceManagementUI extends JPanel {
    private SupermarketService service;
    private JTable invoiceTable;
    private DefaultTableModel invoiceTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JLabel lblCustomer;
    private JLabel lblOrderer;
    private JLabel lblOrderDate;
    private List<Invoice> invoiceList;  // lưu danh sách invoice local

    public InvoiceManagementUI(SupermarketService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // Invoice list panel
        JPanel invoicePanel = new JPanel(new BorderLayout(5,5));
        invoiceTableModel = new DefaultTableModel(new String[]{"Invoice ID","Customer","Orderer","Date"},0);
        invoiceTable = new JTable(invoiceTableModel);
        invoicePanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);
        JButton btnViewDetail = new JButton("Xem chi tiết");
        invoicePanel.add(btnViewDetail, BorderLayout.SOUTH);

        // Detail panel
        JPanel detailPanel = new JPanel(new BorderLayout(5,5));
        JPanel metaPanel = new JPanel(new GridLayout(3,1));
        lblCustomer = new JLabel("Customer: ");
        lblOrderer  = new JLabel("Orderer: ");
        lblOrderDate= new JLabel("Order Date: ");
        metaPanel.add(lblCustomer);
        metaPanel.add(lblOrderer);
        metaPanel.add(lblOrderDate);
        detailPanel.add(metaPanel, BorderLayout.NORTH);
        detailTableModel = new DefaultTableModel(new String[]{"Product","Qty","Unit Price","Discount"},0);
        detailTable = new JTable(detailTableModel);
        detailPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, invoicePanel, detailPanel);
        split.setDividerLocation(400);
        add(split, BorderLayout.CENTER);

        // Load invoices
        loadInvoices();

        // Button action
        btnViewDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewInvoiceDetails();
            }
        });
    }

    private void loadInvoices() {
        try {
            invoiceTableModel.setRowCount(0);
            invoiceList = service.getAllInvoices();  // lưu danh sách
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for (Invoice inv : invoiceList) {
                Customer c = inv.getCustomer();
                User u = inv.getUser();
                invoiceTableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        c != null ? c.getContactName() : "",
                        u != null ? u.getHoTen() : "",
                        inv.getOrderDate() != null ? df.format(inv.getOrderDate()) : ""
                });
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoices.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInvoiceDetails() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice first.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        Invoice inv = invoiceList.get(row);  // lấy từ danh sách đã lưu
        // Hiển thị meta
        lblCustomer.setText("Customer: " + (inv.getCustomer() != null ? inv.getCustomer().getContactName() : ""));
        lblOrderer.setText("Orderer: " + (inv.getUser() != null ? inv.getUser().getHoTen() : ""));
        lblOrderDate.setText("Order Date: " + new SimpleDateFormat("yyyy-MM-dd").format(inv.getOrderDate()));

        try {
            // Load details từ service
            detailTableModel.setRowCount(0);
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            for (InvoiceDetail d : details) {
                detailTableModel.addRow(new Object[]{
                        d.getProduct().getProductName(),
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getDiscount()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoice details.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
