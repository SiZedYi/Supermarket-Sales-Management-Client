package org.example.ui;

import org.example.model.Customer;
import org.example.model.Product;
import org.example.model.User;
import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddInvoiceUI extends JPanel {
    private SupermarketService service;
    private JComboBox<Customer> customerComboBox;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JButton addButton, removeButton, saveButton;
    private JLabel totalLabel;
    private String userId;
    private List<Product> availableProducts = new ArrayList<>();

    public AddInvoiceUI(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;
//        setTitle("Create Invoice");
        setSize(900, 600);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadCustomers();
        loadProducts();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Customer:"));
        customerComboBox = new JComboBox<>();
        topPanel.add(customerComboBox);
        add(topPanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new Object[]{"ProductID", "Name", "UnitPrice", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // only quantity editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 2: return Double.class;
                    case 3: return Integer.class;
                    default: return String.class;
                }
            }
        };

        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Product");
        removeButton = new JButton("Remove Product");
        saveButton = new JButton("Save Invoice");
        totalLabel = new JLabel("Total: $0.0");

        bottomPanel.add(addButton);
        bottomPanel.add(removeButton);
        bottomPanel.add(totalLabel);
        bottomPanel.add(saveButton);

        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showProductSelectionDialog());
        removeButton.addActionListener(e -> removeSelectedProduct());
        saveButton.addActionListener(e -> saveInvoice());
        productTable.getModel().addTableModelListener(e -> updateTotal());
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = service.getAllCustomers();
            for (Customer c : customers) {
                customerComboBox.addItem(c);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Failed to load customers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            availableProducts = service.getAllProducts();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProductSelectionDialog() {
        String[] names = availableProducts.stream().map(Product::getProductName).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Select product:", "Product Selection",
                JOptionPane.PLAIN_MESSAGE, null, names, null);
        if (sel != null) {
            availableProducts.stream()
                    .filter(p -> p.getProductName().equals(sel))
                    .findFirst().ifPresent(p -> {
                        productTableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getUnitPrice(), 1});
                        updateTotal();
                    });
        }
    }

    private void removeSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            productTableModel.removeRow(row);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            double price = (Double) productTableModel.getValueAt(i, 2);
            int qty = (Integer) productTableModel.getValueAt(i, 3);
            total += price * qty;
        }
        totalLabel.setText("Total: $" + total);
    }

    private void saveInvoice() {
        Customer customer = (Customer) customerComboBox.getSelectedItem();
        if (customer == null || productTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer and add at least one product.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Tạo Invoice
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setUser(new User(userId));
            invoice.setOrderDate(new Date());

            // 2. Tạo danh sách InvoiceDetail
            List<InvoiceDetail> details = new ArrayList<>();

            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                Long productId = (Long) productTableModel.getValueAt(i, 0);
                Double unitPrice = (Double) productTableModel.getValueAt(i, 2);
                Integer quantity = (Integer) productTableModel.getValueAt(i, 3);

                Product product = service.getProductById(productId);
                InvoiceDetail detail = new InvoiceDetail();
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setUnitPrice(unitPrice);

                details.add(detail);
            }

            // 3. Gọi service để lưu
            boolean success = service.createInvoice(invoice, details);

            if (success) {
                JOptionPane.showMessageDialog(this, "Invoice created successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create invoice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating invoice: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



}
