package org.example.ui;

import org.example.model.Customer;
import org.example.model.Product;
import org.example.model.User;
import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddInvoiceUI extends JPanel {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Lighter blue
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);      // Red accent
    private static final Color BG_COLOR = new Color(245, 245, 245);        // Light gray bg
    private static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark text
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);      // Light text

    private SupermarketService service;
    private JComboBox<Customer> customerComboBox;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JButton addButton, removeButton, saveButton;
    private JLabel totalLabel;
    private String userId;
    private List<Product> availableProducts = new ArrayList<>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public AddInvoiceUI(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;
        setSize(900, 600);
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        loadCustomers();
        loadProducts();
    }

    private void initUI() {
        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Create New Invoice");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(LIGHT_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Date display on right side of header
        JLabel dateLabel = new JLabel("Date: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(LIGHT_TEXT);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Customer selection panel
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerLabel.setForeground(TEXT_COLOR);

        customerComboBox = new JComboBox<>();
        customerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerComboBox.setPreferredSize(new Dimension(600, 30));
        customerComboBox.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        customerPanel.add(customerLabel);
        customerPanel.add(customerComboBox);

        contentPanel.add(customerPanel, BorderLayout.NORTH);

        // Products table with custom renderer
        productTableModel = new DefaultTableModel(new Object[]{"Product ID", "Product Name", "Unit Price", "Quantity", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only quantity is editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Long.class;
                    case 2: return Double.class;
                    case 3: return Integer.class;
                    case 4: return Double.class;
                    default: return String.class;
                }
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(30);
        productTable.setShowGrid(true);
        productTable.setGridColor(new Color(218, 223, 225));
        productTable.setSelectionBackground(new Color(209, 236, 241));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(new Color(52, 73, 94));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Set column widths
        TableColumnModel columnModel = productTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(120);

        // Currency renderer for price columns
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.RIGHT);
            }

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if (value != null) {
                    value = currencyFormat.format(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        columnModel.getColumn(2).setCellRenderer(currencyRenderer);
        columnModel.getColumn(4).setCellRenderer(currencyRenderer);

        // Center quantity column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        // Table panel with title
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(BG_COLOR);

        JLabel productsLabel = new JLabel("Products");
        productsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productsLabel.setForeground(TEXT_COLOR);
        tablePanel.add(productsLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Actions panel (below table)
        JPanel actionsPanel = new JPanel(new BorderLayout(10, 0));
        actionsPanel.setBackground(BG_COLOR);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Left side - Add/Remove buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(BG_COLOR);

        addButton = createButton("Add Product", SECONDARY_COLOR, LIGHT_TEXT);
        addButton.setIcon(createIcon("plus", 16, LIGHT_TEXT));

        removeButton = createButton("Remove", ACCENT_COLOR, LIGHT_TEXT);
        removeButton.setIcon(createIcon("minus", 16, LIGHT_TEXT));

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);

        // Right side - Total and Save
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        totalPanel.setBackground(BG_COLOR);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(TEXT_COLOR);

        saveButton = createButton("Save Invoice", PRIMARY_COLOR, LIGHT_TEXT);
        saveButton.setIcon(createIcon("save", 16, LIGHT_TEXT));

        totalPanel.add(totalLabel);
        totalPanel.add(saveButton);

        actionsPanel.add(buttonsPanel, BorderLayout.WEST);
        actionsPanel.add(totalPanel, BorderLayout.EAST);

        contentPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Action listeners
        addButton.addActionListener(e -> showProductSelectionDialog());
        removeButton.addActionListener(e -> removeSelectedProduct());
        saveButton.addActionListener(e -> saveInvoice());

        // Update subtotal when quantity changes
        productTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) { // Quantity column
                int row = e.getFirstRow();
                if (row >= 0 && row < productTableModel.getRowCount()) {
                    Double price = (Double) productTableModel.getValueAt(row, 2);
                    Integer qty = (Integer) productTableModel.getValueAt(row, 3);
                    if (qty != null && qty > 0 && price != null) {
                        productTableModel.setValueAt(price * qty, row, 4);
                    } else {
                        productTableModel.setValueAt(0.0, row, 4);
                    }
                }
            }
            updateTotal();
        });
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darken(bgColor, 0.1f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private ImageIcon createIcon(String type, int size, Color color) {
        // This is a simple placeholder method for icons
        // In a real application, you would load actual icons
        return null;
    }

    private Color darken(Color color, float fraction) {
        int red = Math.max(0, Math.round(color.getRed() * (1 - fraction)));
        int green = Math.max(0, Math.round(color.getGreen() * (1 - fraction)));
        int blue = Math.max(0, Math.round(color.getBlue() * (1 - fraction)));
        return new Color(red, green, blue);
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = service.getAllCustomers();
            for (Customer c : customers) {
                customerComboBox.addItem(c);
            }
        } catch (RemoteException e) {
            showError("Failed to load customers: " + e.getMessage());
        }
    }

    private void loadProducts() {
        try {
            availableProducts = service.getAllProducts();
        } catch (RemoteException e) {
            showError("Failed to load products: " + e.getMessage());
        }
    }

    private void showProductSelectionDialog() {
        loadProducts(); // Refresh product list

        // Create a more stylish product selection dialog
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Select Product", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel("Select a Product");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(LIGHT_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search field
        JTextField searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        headerPanel.add(searchField, BorderLayout.EAST);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Product list
        DefaultListModel<Product> productListModel = new DefaultListModel<>();
        availableProducts.forEach(productListModel::addElement);

        JList<Product> productList = new JList<>(productListModel);
        productList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Product product = (Product) value;
                String text = String.format("<html><b>%s</b><br><small>%s</small></html>",
                        product.getProductName(),
                        currencyFormat.format(product.getUnitPrice()));

                Component c = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);

                if (isSelected) {
                    setBackground(new Color(209, 236, 241));
                    setForeground(TEXT_COLOR);
                } else {
                    setBackground(Color.WHITE);
                }

                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 223, 225)));
                setPreferredSize(new Dimension(0, 50));

                return c;
            }
        });

        JScrollPane listScrollPane = new JScrollPane(productList);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder());
        dialog.add(listScrollPane, BorderLayout.CENTER);

        // Search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }

            private void filterProducts() {
                String searchText = searchField.getText().toLowerCase();
                productListModel.clear();

                for (Product p : availableProducts) {
                    if (p.getProductName().toLowerCase().contains(searchText)) {
                        productListModel.addElement(p);
                    }
                }
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton addButton = new JButton("Add to Invoice");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(LIGHT_TEXT);
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> {
            Product selectedProduct = productList.getSelectedValue();
            if (selectedProduct != null) {
                productTableModel.addRow(new Object[]{
                        selectedProduct.getProductId(),
                        selectedProduct.getProductName(),
                        selectedProduct.getUnitPrice(),
                        1,
                        selectedProduct.getUnitPrice()
                });
                updateTotal();
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Double click to select
        productList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addButton.doClick();
                }
            }
        });

        dialog.setVisible(true);
    }

    private void removeSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            productTableModel.removeRow(row);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product to remove.",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            Object subtotalObj = productTableModel.getValueAt(i, 4);
            if (subtotalObj != null) {
                total += (Double) subtotalObj;
            }
        }
        totalLabel.setText("Total: " + currencyFormat.format(total));
    }

    private void saveInvoice() {
        Customer customer = (Customer) customerComboBox.getSelectedItem();
        if (customer == null || productTableModel.getRowCount() == 0) {
            showWarning("Please select a customer and add at least one product.");
            return;
        }

        try {
            // Create Invoice
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setUser(new User(userId));
            invoice.setOrderDate(new Date());

            // Create InvoiceDetails
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

            // Call service to save
            boolean success = service.createInvoice(invoice, details);

            if (success) {
                showSuccess("Invoice created successfully");
                // Clear the form
                while (productTableModel.getRowCount() > 0) {
                    productTableModel.removeRow(0);
                }
                updateTotal();
            } else {
                showError("Failed to create invoice.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            showError("Error creating invoice: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}