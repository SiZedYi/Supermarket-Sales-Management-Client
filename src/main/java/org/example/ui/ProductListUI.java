package org.example.ui;

import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.List;

public class ProductListUI extends JPanel {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Category> cbFilterCategory;
    private JTextField searchField;

    // Colors
    private final Color PRIMARY_COLOR = new Color(56, 142, 60);
    private final Color SECONDARY_COLOR = new Color(46, 125, 50);
    private final Color ACCENT_COLOR = new Color(245, 124, 0);
    private final Color BACKGROUND_COLOR = new Color(248, 248, 248);
    private final Color TABLE_HEADER_COLOR = new Color(76, 175, 80);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public ProductListUI(SupermarketService service) {
        this.service = service;
        setSize(950, 600);
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main container with card-like effect
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header with title and search components
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel containing table with products
        JPanel centerPanel = createTablePanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel actionPanel = createActionPanel();
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        loadCategoriesToFilter();
        loadProducts();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(createPanelBorder());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));

        // Title and subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));

        JLabel titleLabel = new JLabel("Product Inventory");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Manage your product catalog");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Search and filter panel
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        searchFilterPanel.setBackground(Color.WHITE);
        searchFilterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));

        // Category filter
        JLabel filterLabel = new JLabel("Category:");
        filterLabel.setFont(NORMAL_FONT);
        filterLabel.setForeground(new Color(80, 80, 80));

        cbFilterCategory = new JComboBox<>();
        cbFilterCategory.setFont(NORMAL_FONT);
        cbFilterCategory.setPreferredSize(new Dimension(180, 35));
        cbFilterCategory.setBackground(Color.WHITE);
        cbFilterCategory.setFocusable(false);

        // Custom renderer for the combo box
        cbFilterCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Category) {
                    Category category = (Category) value;
                    setText(category.getCategoryId() == null ? "All Categories" : category.getCategoryName());
                }

                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(60, 60, 60));
                }

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        // Add "All Categories" as first item
        cbFilterCategory.addItem(new Category());

        // Search field with rounded border
        searchField = new JTextField(15);
        searchField.setFont(NORMAL_FONT);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Add placeholder text to search field
        searchField.setText("Search products...");
        searchField.setForeground(new Color(180, 180, 180));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products...");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });

        // Search button with icon
        JButton searchButton = createButton("Search", ACCENT_COLOR);
        searchButton.setPreferredSize(new Dimension(100, 35));

        // Add action listeners
        cbFilterCategory.addActionListener(e -> filterProducts());
        searchField.addActionListener(e -> filterProducts());
        searchButton.addActionListener(e -> filterProducts());

        searchFilterPanel.add(filterLabel);
        searchFilterPanel.add(cbFilterCategory);
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchButton);

        headerPanel.add(searchFilterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(createPanelBorder());

        // Create table model with non-editable cells
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Product Name", "Price", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(NORMAL_FONT);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(232, 245, 233));
        table.setSelectionForeground(SECONDARY_COLOR);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(null);

        // Custom renderers for columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // ID column - centered
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        // Name column - left aligned with padding
        DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        };
        table.getColumnModel().getColumn(1).setCellRenderer(nameRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Price column - custom renderer for currency format
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormat.format(value);
                }
                return super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
            }
        };
        priceRenderer.setHorizontalAlignment(JLabel.RIGHT);
        priceRenderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        table.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Category column - centered
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Add stripe effect to table rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component comp = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    comp.setForeground(new Color(60, 60, 60));
                }

                setBorder(noFocusBorder);
                return comp;
            }
        });

        // Add double-click listener for viewing details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewProductDetails();
                }
            }
        });

        // Create scroll pane with custom border
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Status panel at the bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel statusLabel = new JLabel("Double-click on a product to view details");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel("");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(100, 100, 100));

        // Update the count label when the table model changes
        tableModel.addTableModelListener(e -> {
            countLabel.setText(tableModel.getRowCount() + " products");
        });

        statusPanel.add(countLabel, BorderLayout.EAST);

        tablePanel.add(statusPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = createButton("Refresh", new Color(100, 181, 246));
        refreshButton.setIcon(createRefreshIcon());

        JButton addButton = createButton("Add Product", PRIMARY_COLOR);
        addButton.setIcon(createAddIcon());

        JButton deleteButton = createButton("Delete", new Color(229, 57, 53));
        deleteButton.setIcon(createDeleteIcon());

        // Add action listeners
        refreshButton.addActionListener(e -> loadProducts());

        addButton.addActionListener(e -> {
            new ProductManagementUI(service, this).setVisible(true);
        });

        deleteButton.addActionListener(e -> deleteSelectedProduct());

        actionPanel.add(refreshButton);
        actionPanel.add(addButton);
        actionPanel.add(deleteButton);

        return actionPanel;
    }

    // Helper method to create styled buttons
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        // Add rounded corners and shadow
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darker(bgColor, 0.9f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Helper method to create panel border with shadow effect
    private Border createPanelBorder() {
        Border lineBorder = BorderFactory.createLineBorder(new Color(230, 230, 230));
        Border emptyBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        return BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
    }

    // Helper method to create simple icons
    private ImageIcon createAddIcon() {
        return createIcon("\u002B", 14, Color.WHITE);
    }

    private ImageIcon createDeleteIcon() {
        return createIcon("\u2716", 14, Color.WHITE);
    }

    private ImageIcon createRefreshIcon() {
        return createIcon("\u21BB", 14, Color.WHITE);
    }

    private ImageIcon createIcon(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(color);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(size + 2, size + 2));

        BufferedImage image = new BufferedImage(
                size + 2, size + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        label.paint(g2);
        g2.dispose();

        return new ImageIcon(image);
    }

    // Helper method to darken colors for hover effects
    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0),
                color.getAlpha());
    }

    // Data loading methods
    public void loadProducts() {
        try {
            tableModel.setRowCount(0); // clear table
            List<Product> products = service.getAllProducts();
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getUnitPrice(),
                        product.getCategory() != null ? product.getCategory().getCategoryName() : "Uncategorized"
                });
            }

            // Display product count
            showStatusMessage(products.size() + " products loaded");
        } catch (RemoteException e) {
            e.printStackTrace();
            showErrorDialog("Failed to load products", e.getMessage());
        }
    }

    public void filterProducts() {
        try {
            tableModel.setRowCount(0); // Clear table first

            List<Product> products = service.getAllProducts();
            Category selectedCategory = (Category) cbFilterCategory.getSelectedItem();
            String searchText = searchField.getText().toLowerCase();

            if (searchText.equals("search products...")) {
                searchText = "";
            }

            int matchCount = 0;

            for (Product product : products) {
                boolean categoryMatch = selectedCategory.getCategoryId() == null ||
                        (product.getCategory() != null &&
                                selectedCategory.getCategoryId().equals(product.getCategory().getCategoryId()));

                boolean searchMatch = searchText.isEmpty() ||
                        (product.getProductName() != null &&
                                product.getProductName().toLowerCase().contains(searchText));

                if (categoryMatch && searchMatch) {
                    tableModel.addRow(new Object[]{
                            product.getProductId(),
                            product.getProductName(),
                            product.getUnitPrice(),
                            product.getCategory() != null ? product.getCategory().getCategoryName() : "Uncategorized"
                    });
                    matchCount++;
                }
            }

            // Update status message
            String message = matchCount + " products found";
            if (!searchText.isEmpty()) {
                message += " matching '" + searchText + "'";
            }
            if (selectedCategory.getCategoryId() != null) {
                message += " in category '" + selectedCategory.getCategoryName() + "'";
            }

            showStatusMessage(message);

        } catch (RemoteException e) {
            e.printStackTrace();
            showErrorDialog("Filter Error", "Failed to filter products: " + e.getMessage());
        }
    }

    private void loadCategoriesToFilter() {
        try {
            List<Category> categories = service.listCategories();
            for (Category category : categories) {
                cbFilterCategory.addItem(category);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            showErrorDialog("Error Loading Categories", e.getMessage());
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = tableModel.getValueAt(selectedRow, 0).toString();
            String productName = tableModel.getValueAt(selectedRow, 1).toString();

            // Create custom confirmation dialog
            int result = showConfirmDialog(
                    "Delete Product",
                    "Are you sure you want to delete product: " + productName + "?",
                    "This action cannot be undone.");

            if (result == JOptionPane.YES_OPTION) {
                try {
                    service.deleteProduct(Long.parseLong(productId));
                    showSuccessDialog("Product Deleted", "Product successfully removed from inventory.");
                    loadProducts(); // Refresh table
                } catch (RemoteException e) {
                    e.printStackTrace();
                    showErrorDialog("Delete Error", "Failed to delete product: " + e.getMessage());
                }
            }
        } else {
            showWarningDialog("No Selection", "Please select a product to delete.");
        }
    }

    private void viewProductDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = tableModel.getValueAt(selectedRow, 0).toString();
            try {
                // Implement product details view
                // This is a placeholder - you would implement actual details viewing functionality
                showInfoDialog("Product Details",
                        "Viewing details for product ID: " + productId + "\n" +
                                "Name: " + tableModel.getValueAt(selectedRow, 1) + "\n" +
                                "Price: " + tableModel.getValueAt(selectedRow, 2) + "\n" +
                                "Category: " + tableModel.getValueAt(selectedRow, 3));
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Error", "Failed to load product details: " + e.getMessage());
            }
        }
    }

    // Helper methods for status messages and dialogs
    private void showStatusMessage(String message) {
        // This would update a status bar if you have one
        // For now, just print to console
        System.out.println("Status: " + message);
    }

    private void showSuccessDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, new Color(46, 125, 50));
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, new Color(198, 40, 40));
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, new Color(255, 152, 0));
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showInfoDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, new Color(25, 118, 210));
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private int showConfirmDialog(String title, String mainMessage, String subMessage) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel mainLabel = new JLabel(mainMessage);
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel subLabel = new JLabel(subMessage);
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subLabel.setForeground(new Color(150, 150, 150));

        panel.add(mainLabel, BorderLayout.NORTH);
        panel.add(subLabel, BorderLayout.CENTER);

        return JOptionPane.showConfirmDialog(
                this, panel, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    private JPanel createDialogPanel(String message, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create color stripe on the left
        JPanel colorStripe = new JPanel();
        colorStripe.setBackground(accentColor);
        colorStripe.setPreferredSize(new Dimension(5, 0));

        // Message with proper font
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(NORMAL_FONT);

        panel.add(colorStripe, BorderLayout.WEST);
        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }
}