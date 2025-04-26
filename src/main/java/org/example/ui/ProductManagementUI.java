package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.*;
import java.rmi.RemoteException;
import java.util.List;

import java.awt.*;
import java.awt.event.ActionEvent;


public class ProductManagementUI extends JFrame {
    private SupermarketService service;
    private JTextField nameField, priceField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Supplier> supplierComboBox;
    private JButton addButton;
    private ProductListUI productListUI;

    public ProductManagementUI(SupermarketService service, ProductListUI productListUI) {
        this.service = service;
        this.productListUI = productListUI;
        setTitle("Product Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));


        nameField = new JTextField();
        priceField = new JTextField();
        categoryComboBox = new JComboBox<>();
        supplierComboBox = new JComboBox<>();
        addButton = new JButton("Thêm sản phẩm");

        loadCategories();
        loadSuppliers();

        add(new JLabel("Tên sản phẩm:"));
        add(nameField);
        add(new JLabel("Giá:"));
        add(priceField);
        add(new JLabel("Loại hàng:"));
        add(categoryComboBox);
        add(new JLabel("Nhà cung cấp:"));
        add(supplierComboBox);
        add(new JLabel());
        add(addButton);

        addButton.addActionListener(this::addProduct);
    }

    private void loadCategories() {
        try {
            List<Category> categories = service.listCategories();
            for (Category c : categories) {
                categoryComboBox.addItem(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        }
    }

    private void addProduct(ActionEvent event) {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();

            if (name.isEmpty() || selectedCategory == null || selectedSupplier == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            Product product = new Product();
            product.setProductName(name);
            product.setUnitPrice(price);
            product.setCategory(selectedCategory);
            product.setSupplier(selectedSupplier);

            service.addProduct(product);
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            if (productListUI != null) {
                productListUI.loadProducts(); // Refresh product list UI
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại: " + e.getMessage());
        }
    }
}