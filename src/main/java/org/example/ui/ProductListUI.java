package org.example.ui;

import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;
public class ProductListUI extends JFrame {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Category> cbFilterCategory;


    public ProductListUI(SupermarketService service) {
        this.service = service;
        setTitle("Product List");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Category"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnManageProducts = new JButton("Manage Products");
        JButton btnDeleteProduct = new JButton("Delete Product");

        btnManageProducts.addActionListener(e -> {
            new ProductManagementUI(service, this).setVisible(true);
        });

        btnDeleteProduct.addActionListener(e -> deleteSelectedProduct());

        buttonPanel.add(btnManageProducts);
        buttonPanel.add(btnDeleteProduct);

        add(buttonPanel, BorderLayout.SOUTH);

        // Panel chứa bộ lọc
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter by Category:"));
        cbFilterCategory = new JComboBox<>();
        cbFilterCategory.addItem(new Category()); // "Tất cả" danh mục
        loadCategoriesToFilter();
        cbFilterCategory.addActionListener(e -> filterProducts());
        topPanel.add(cbFilterCategory);

        add(topPanel, BorderLayout.NORTH);


        loadProducts();
    }

    public void loadProducts() {
        try {
            tableModel.setRowCount(0); // clear table
            List<Product> products = service.getAllProducts(); // cần thêm hàm listProducts() trong SupermarketService
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getUnitPrice(),
                        product.getCategory().getCategoryName() // Giả sử Product có Category
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    public void filterProducts() {
        try {
            tableModel.setRowCount(0); // Clear bảng trước

            List<Product> products = service.getAllProducts();
            Category selectedCategory = (Category) cbFilterCategory.getSelectedItem();

            for (Product product : products) {
                if (selectedCategory.getCategoryId() == null ||
                        (product.getCategory() != null && selectedCategory.getCategoryId().equals(product.getCategory().getCategoryId()))) {
                    tableModel.addRow(new Object[]{
                            product.getProductId(), product.getProductName(), product.getUnitPrice(),
                            product.getCategory() != null ? product.getCategory().getCategoryName() : ""
                    });
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }


    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long productId = (Long) tableModel.getValueAt(selectedRow, 0); // ID ở cột 0
            try {
                service.deleteProduct(productId);
                JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                loadProducts(); // Refresh table
            } catch (RemoteException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete product: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
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
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

}
