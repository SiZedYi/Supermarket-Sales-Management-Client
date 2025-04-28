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
import java.text.ParseException;
import java.util.List;

public class ProductManagementUI extends JFrame {
    private SupermarketService service;
    private JTextField nameField;
    private JFormattedTextField priceField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Supplier> supplierComboBox;
    private JButton addButton, cancelButton;
    private ProductListUI productListUI;
    private JTextArea descriptionArea;

    // Colors
    private final Color PRIMARY_COLOR = new Color(56, 142, 60);
    private final Color SECONDARY_COLOR = new Color(46, 125, 50);
    private final Color ACCENT_COLOR = new Color(245, 124, 0);
    private final Color ERROR_COLOR = new Color(211, 47, 47);
    private final Color LIGHT_BG = new Color(248, 248, 248);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public ProductManagementUI(SupermarketService service, ProductListUI productListUI) {
        this.service = service;
        this.productListUI = productListUI;

        setTitle("Add New Product");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set window icon (if available)
        try {
            ImageIcon icon = createProductIcon();
            if (icon != null) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // Fallback if icon creation fails
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load data for dropdowns
        loadCategories();
        loadSuppliers();

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Add New Product");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Enter product details below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        // Add a horizontal line under the header
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        panel.add(separator, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        // Setup input fields with enhanced styling
        nameField = createStyledTextField();

        // Create formatted text field for price
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        priceField = new JFormattedTextField(format);
        styleTextField(priceField);

        descriptionArea = new JTextArea();
        descriptionArea.setFont(INPUT_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descScrollPane.setPreferredSize(new Dimension(0, 80));

        categoryComboBox = createStyledComboBox();
        supplierComboBox = createStyledComboBox();

        // Create form panel with grid bag layout for more control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(10, 0, 5, 15);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(10, 0, 5, 0);

        // Add form components
        formPanel.add(createFormLabel("Product Name:"), createGbc(0, 0));
        formPanel.add(nameField, createGbc(1, 0));

        formPanel.add(createFormLabel("Price:"), createGbc(0, 1));
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(Color.WHITE);
        JLabel currencyLabel = new JLabel("đ");
        currencyLabel.setFont(INPUT_FONT);
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        pricePanel.add(currencyLabel, BorderLayout.WEST);
        pricePanel.add(priceField, BorderLayout.CENTER);
        formPanel.add(pricePanel, createGbc(1, 1));

        formPanel.add(createFormLabel("Category:"), createGbc(0, 2));
        formPanel.add(categoryComboBox, createGbc(1, 2));

        formPanel.add(createFormLabel("Supplier:"), createGbc(0, 3));
        formPanel.add(supplierComboBox, createGbc(1, 3));

        formPanel.add(createFormLabel("Description:"), createGbc(0, 4));
        formPanel.add(descScrollPane, createGbc(1, 4));

        // Create a container panel with a card-like effect
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(LIGHT_BG);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        cardPanel.add(formPanel, BorderLayout.CENTER);
        containerPanel.add(cardPanel, BorderLayout.CENTER);

        return containerPanel;
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = x == 0 ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = x == 0 ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL;

        gbc.weightx = x == 0 ? 0.0 : 1.0;
        gbc.insets = x == 0
                ? new Insets(12, 10, 12, 15) // Label insets
                : new Insets(12, 0, 12, 10);  // Field insets

        return gbc;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        cancelButton = createButton("Cancel", new Color(120, 120, 120));
        addButton = createButton("Add Product", PRIMARY_COLOR);

        // Add icons to buttons
        try {
            addButton.setIcon(createIcon("\u002B", 16, Color.WHITE)); // Plus sign
        } catch (Exception e) {
            // Fallback if icon creation fails
        }

        cancelButton.addActionListener(e -> dispose());
        addButton.addActionListener(this::addProduct);

        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(INPUT_FONT);
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Add focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(INPUT_FONT);
        comboBox.setPreferredSize(new Dimension(0, 40));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Custom renderer for better appearance
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                if (value instanceof Category) {
                    setText(((Category) value).getCategoryName());
                } else if (value instanceof Supplier) {
                    setText(((Supplier) value).getCompanyName());
                }

                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(70, 70, 70));
                }

                return this;
            }
        });

        return comboBox;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));

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

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(darker(bgColor, 0.8f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(darker(bgColor, 0.9f));
            }
        });

        return button;
    }

    private void loadCategories() {
        try {
            List<Category> categories = service.listCategories();
            for (Category c : categories) {
                categoryComboBox.addItem(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error Loading Categories", e.getMessage());
        }
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = service.listSuppliers();
            for (Supplier s : suppliers) {
                supplierComboBox.addItem(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error Loading Suppliers", e.getMessage());
        }
    }

    private void addProduct(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            double price = ((Number) priceField.getValue()).doubleValue();
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
            String description = descriptionArea.getText().trim();

            Product product = new Product();
            product.setProductName(name);
            product.setUnitPrice(price);
            product.setCategory(selectedCategory);
            product.setSupplier(selectedSupplier);
            // Set description if your Product class has this field
            // product.setDescription(description);

            service.addProduct(product);
            showSuccessDialog("Product Added", "Product has been added successfully!");

            if (productListUI != null) {
                productListUI.loadProducts(); // Refresh product list UI
                dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Add Product Failed", e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("• Product name is required\n");
            highlightError(nameField);
        }

        try {
            if (priceField.getValue() == null) {
                errors.append("• Price is required\n");
                highlightError(priceField);
            } else {
                double price = ((Number) priceField.getValue()).doubleValue();
                if (price <= 0) {
                    errors.append("• Price must be greater than zero\n");
                    highlightError(priceField);
                }
            }
        } catch (Exception e) {
            errors.append("• Invalid price format\n");
            highlightError(priceField);
        }

        if (categoryComboBox.getSelectedItem() == null) {
            errors.append("• Category must be selected\n");
            highlightError(categoryComboBox);
        }

        if (supplierComboBox.getSelectedItem() == null) {
            errors.append("• Supplier must be selected\n");
            highlightError(supplierComboBox);
        }

        if (errors.length() > 0) {
            showValidationErrorDialog("Please correct the following errors:", errors.toString());
            return false;
        }

        return true;
    }

    private void highlightError(JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR_COLOR, 2, true),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)
        ));

        // Reset after a delay
        Timer timer = new Timer(2000, e -> {
            if (component instanceof JTextField) {
                styleTextField((JTextField) component);
            } else if (component instanceof JComboBox) {
                component.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Helper method to darken colors for hover effects
    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0),
                color.getAlpha());
    }

    private void showSuccessDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, PRIMARY_COLOR);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JPanel panel = createDialogPanel(message, ERROR_COLOR);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showValidationErrorDialog(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ERROR_COLOR);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setEditable(false);
        messageArea.setBackground(panel.getBackground());
        messageArea.setBorder(null);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageArea, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Validation Error", JOptionPane.ERROR_MESSAGE);
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
        messageLabel.setFont(INPUT_FONT);

        panel.add(colorStripe, BorderLayout.WEST);
        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    // Simple product icon creation
    private ImageIcon createProductIcon() {
        int size = 24;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY_COLOR);
        g2.fillRect(2, 6, size - 4, size - 8);
        g2.setColor(Color.WHITE);
        g2.fillRect(5, 9, size - 10, size - 14);
        g2.dispose();

        return new ImageIcon(image);
    }

    private Icon createIcon(String text, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("Segoe UI Symbol", Font.BOLD, size));
                g2d.setColor(color);
                g2d.drawString(text, x, y + size);
                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}