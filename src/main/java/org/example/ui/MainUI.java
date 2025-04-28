package org.example.ui;

import org.example.rmi.SupermarketService;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainUI(SupermarketService service, String userId) {
        setTitle("Supermarket Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar (ManagerDashboard)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JButton btnViewInfo = new JButton("View User Info");
        JButton btnManageProducts = new JButton("Manage Products");
        JButton btnManageInvoices = new JButton("Manage Invoices");
        JButton btnManageAddInvoices = new JButton("Add to Invoices");
        JButton btnManageEmployees = new JButton("Manage Employees");

        sidebar.add(btnViewInfo);
        sidebar.add(btnManageProducts);
        sidebar.add(btnManageInvoices);
        sidebar.add(btnManageAddInvoices);
        sidebar.add(btnManageEmployees);

        add(sidebar, BorderLayout.WEST);

        // Main content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add different panels to the CardLayout
        contentPanel.add(new AccountInfoUI(service, userId), "USER"); // Default empty panel
        contentPanel.add(new ProductListUI(service), "PRODUCTS");
        contentPanel.add(new InvoiceManagementUI(service), "INVOICES-LIST");
        contentPanel.add(new AddInvoiceUI(service, userId), "INVOICES");
        contentPanel.add(new EmployeeManagementUI(service), "EMPLOYEES");

        add(contentPanel, BorderLayout.CENTER);

        // Button actions to switch content
        btnViewInfo.addActionListener(e -> cardLayout.show(contentPanel, "USER"));
        btnManageProducts.addActionListener(e -> cardLayout.show(contentPanel, "PRODUCTS"));
        btnManageInvoices.addActionListener(e -> cardLayout.show(contentPanel, "INVOICES-LIST"));
        btnManageAddInvoices.addActionListener(e -> cardLayout.show(contentPanel, "INVOICES"));
        btnManageEmployees.addActionListener(e -> cardLayout.show(contentPanel, "EMPLOYEES"));
    }
}