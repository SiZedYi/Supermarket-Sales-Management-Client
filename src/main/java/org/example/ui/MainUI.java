package org.example.ui;

import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private final Color SIDEBAR_BG = new Color(50, 50, 70);
    private final Color SIDEBAR_TEXT = new Color(220, 220, 220);
    private final Color SIDEBAR_HOVER = new Color(70, 70, 90);
    private final Color SIDEBAR_SELECTED = new Color(80, 120, 170);
    private final Color CONTENT_BG = new Color(245, 245, 250);
    private final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font MENU_FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final int SIDEBAR_WIDTH = 230;

    private Map<String, JPanel> menuItems = new HashMap<>();
    private JPanel activeMenuItem = null;
    private String userId;
    private SupermarketService service;

    public MainUI(SupermarketService service, String userId) {
        this.service = service;
        this.userId = userId;

        setupFrame();
        createSidebar();
        createContentPanel();
        addWindowListeners();

        // Set USER as the default view
        showPanel("USER");
    }

    private void setupFrame() {
        setTitle("Supermarket Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CONTENT_BG);

        // Set minimum size
        setMinimumSize(new Dimension(900, 600));
    }

    private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(40, 40, 60)));

        // Header panel with logo and title
        JPanel headerPanel = createSidebarHeader();
        sidebar.add(headerPanel, BorderLayout.NORTH);

        // Menu items panel
        JPanel menuPanel = createMenuPanel();
        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Footer panel with logout button
        JPanel footerPanel = createSidebarFooter();
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private JPanel createSidebarHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SIDEBAR_BG.darker());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        headerPanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 80));

        // Title
        JLabel titleLabel = new JLabel("Supermarket");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 180, 200));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(SIDEBAR_BG.darker());
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Logo (simple icon)
        JLabel logoLabel = new JLabel(createLogo());

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private ImageIcon createLogo() {
        int size = 40;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(100, 180, 220));
        g2d.fillRoundRect(0, 0, size, size, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));

        // Draw a shopping cart icon
        int[] xPoints = {8, 16, 32, 28, 12};
        int[] yPoints = {30, 12, 18, 30, 30};
        g2d.drawPolyline(xPoints, yPoints, 5);

        // Draw wheels
        g2d.fillOval(12, 32, 6, 6);
        g2d.fillOval(28, 32, 6, 6);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // User info section
        createMenuItem(menuPanel, "USER", "User Information", "user_icon");

        // Separator
        addSeparator(menuPanel);

        // Add category label
        addCategoryLabel(menuPanel, "MANAGEMENT");

        // Main menu items
        createMenuItem(menuPanel, "PRODUCTS", "Manage Products", "product_icon");
        createMenuItem(menuPanel, "INVOICES-LIST", "Invoice List", "invoice_list_icon");
        createMenuItem(menuPanel, "INVOICES", "Create Invoice", "new_invoice_icon");
        createMenuItem(menuPanel, "EMPLOYEES", "Manage Employees", "employee_icon");

        // Glue component to push everything up
        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private void addCategoryLabel(JPanel menuPanel, String text) {
        JLabel categoryLabel = new JLabel("   " + text);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        categoryLabel.setForeground(new Color(150, 150, 170));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        menuPanel.add(categoryLabel);
    }

    private void addSeparator(JPanel menuPanel) {
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(70, 70, 90));
        separator.setForeground(new Color(70, 70, 90));
        separator.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 30, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel separatorPanel = new JPanel();
        separatorPanel.setLayout(new BoxLayout(separatorPanel, BoxLayout.Y_AXIS));
        separatorPanel.setBackground(SIDEBAR_BG);
        separatorPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        separatorPanel.add(separator);

        menuPanel.add(separatorPanel);
    }

    private void createMenuItem(JPanel menuPanel, String key, String text, String iconName) {
        JPanel menuItem = new JPanel();
        menuItem.setLayout(new BoxLayout(menuItem, BoxLayout.X_AXIS));
        menuItem.setBackground(SIDEBAR_BG);
        menuItem.setBorder(new EmptyBorder(10, 15, 10, 15));
        menuItem.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 45));
        menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon (placeholder)
        JLabel iconLabel = new JLabel(createMenuIcon(iconName));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(MENU_FONT);
        textLabel.setForeground(SIDEBAR_TEXT);

        menuItem.add(iconLabel);
        menuItem.add(textLabel);
        menuItem.add(Box.createHorizontalGlue());

        // Store in map
        menuItems.put(key, menuItem);

        // Add hover effect
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeMenuItem != menuItem) {
                    menuItem.setBackground(SIDEBAR_HOVER);
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeMenuItem != menuItem) {
                    menuItem.setBackground(SIDEBAR_BG);
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(key);
            }
        });

        menuPanel.add(menuItem);
    }

    private ImageIcon createMenuIcon(String iconName) {
        int size = 20;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(SIDEBAR_TEXT);

        switch (iconName) {
            case "user_icon":
                // User icon
                g2d.drawOval(5, 2, 10, 10);
                g2d.drawArc(2, 12, 16, 16, 40, 100);
                break;
            case "product_icon":
                // Product icon
                g2d.drawRect(3, 3, 14, 14);
                g2d.drawLine(3, 8, 17, 8);
                break;
            case "invoice_list_icon":
                // Invoice list icon
                g2d.drawRect(3, 2, 14, 16);
                g2d.drawLine(6, 6, 14, 6);
                g2d.drawLine(6, 10, 14, 10);
                g2d.drawLine(6, 14, 14, 14);
                break;
            case "new_invoice_icon":
                // New invoice icon
                g2d.drawRect(3, 2, 14, 16);
                g2d.drawLine(10, 6, 10, 14);
                g2d.drawLine(6, 10, 14, 10);
                break;
            case "employee_icon":
                // Employee icon
                g2d.drawOval(7, 2, 6, 6);
                g2d.drawRoundRect(2, 10, 16, 8, 3, 3);
                break;
            default:
                // Default icon
                g2d.drawRect(4, 4, 12, 12);
                break;
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    private JPanel createSidebarFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(SIDEBAR_BG.darker());
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        footerPanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 60));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(180, 70, 70));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(200, 80, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(180, 70, 70));
            }
        });

        // Logout action
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    MainUI.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Open login form or perform logout actions
                try {
                    Class<?> loginFormClass = Class.forName("org.example.LoginForm");
                    JFrame loginForm = (JFrame) loginFormClass.getDeclaredConstructor().newInstance();
                    loginForm.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            }
        });

        footerPanel.add(logoutButton, BorderLayout.CENTER);

        return footerPanel;
    }

    private void createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add different panels to the CardLayout
        contentPanel.add(new AccountInfoUI(service, userId), "USER");
        contentPanel.add(new ProductListUI(service), "PRODUCTS");
        contentPanel.add(new InvoiceManagementUI(service), "INVOICES-LIST");
        contentPanel.add(new AddInvoiceUI(service, userId), "INVOICES");
        contentPanel.add(new EmployeeManagementUI(service), "EMPLOYEES");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void showPanel(String key) {
        // Update sidebar selection
        if (activeMenuItem != null) {
            activeMenuItem.setBackground(SIDEBAR_BG);
            ((JLabel)activeMenuItem.getComponent(1)).setFont(MENU_FONT);
        }

        JPanel selectedItem = menuItems.get(key);
        if (selectedItem != null) {
            selectedItem.setBackground(SIDEBAR_SELECTED);
            ((JLabel)selectedItem.getComponent(1)).setFont(MENU_FONT_BOLD);
            activeMenuItem = selectedItem;
        }

        // Show the selected panel
        cardLayout.show(contentPanel, key);

        // Update window title based on selection
        updateWindowTitle(key);
    }

    private void updateWindowTitle(String key) {
        String baseTitle = "Supermarket Management System";
        String section = "";

        switch (key) {
            case "USER":
                section = "User Information";
                break;
            case "PRODUCTS":
                section = "Product Management";
                break;
            case "INVOICES-LIST":
                section = "Invoice Management";
                break;
            case "INVOICES":
                section = "Create Invoice";
                break;
            case "EMPLOYEES":
                section = "Employee Management";
                break;
        }

        setTitle(baseTitle + " - " + section);
    }

    private void addWindowListeners() {
        // Add resize listener to adjust UI components if needed
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update component sizes if needed
                revalidate();
                repaint();
            }
        });
    }
}