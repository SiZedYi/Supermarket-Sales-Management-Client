package org.example.ui;
import org.example.model.*;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmployeeManagementUI extends JPanel {
    private SupermarketService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TABLE_CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public EmployeeManagementUI(SupermarketService service) {
        this.service = service;
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel with table
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Load employee data
        loadEmployees();
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);

        // Create table with custom model - Removed Actions column
        String[] columnNames = {"UserId", "Full Name", "Phone", "Email", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable columns
            }
        };

        table = new JTable(tableModel);
        table.setFont(TABLE_CONTENT_FONT);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 246, 255));
        table.setSelectionForeground(PRIMARY_COLOR);
        table.setFocusable(false);

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center align the cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Hide userId column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(0).setResizable(false);

        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Create a scroll pane with custom border
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel statusLabel = new JLabel("Displaying all employees");
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        contentPanel.add(statusPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Employee");
        addButton.setIcon(createIcon("\u002B", 16, Color.WHITE)); // Plus sign
        styleButton(addButton, PRIMARY_COLOR);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setIcon(createIcon("\u21BB", 16, new Color(70, 70, 70))); // Refresh icon
        styleButton(refreshButton, new Color(240, 240, 240), new Color(70, 70, 70));

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);

        addButton.addActionListener(e -> addEmployee());
        refreshButton.addActionListener(e -> loadEmployees());

        return buttonPanel;
    }

    private void styleButton(JButton button, Color background) {
        styleButton(button, background, Color.WHITE);
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
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

    public void loadEmployees() {
        try {
            List<User> users = service.listEmployees();
            tableModel.setRowCount(0);
            int rowIndex = 0;

            for (User emp : users) {
                tableModel.addRow(new Object[]{
                        emp.getUserId(),
                        emp.getHoTen(),
                        emp.getCccd(),
                        emp.getEmail(),
                        emp.getUserId().startsWith("SA") ? "Sales Agent" : "User"
                });

                // Apply alternating row colors
                if (rowIndex % 2 == 1) {
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        table.prepareRenderer(table.getCellRenderer(rowIndex, i), rowIndex, i)
                                .setBackground(new Color(250, 250, 250));
                    }
                }
                rowIndex++;
            }

        } catch (Exception e) {
            showErrorMessage("Error loading employees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        try {
            AddUserUI addUserUI = new AddUserUI(service, this);
            // Set modern look for the dialog
            addUserUI.setSize(600, 650);
            addUserUI.setLocationRelativeTo(this);
            addUserUI.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Error opening Add Employee dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccessMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(46, 125, 50));

        panel.add(label, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Success", JOptionPane.PLAIN_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(198, 40, 40));

        panel.add(label, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(13, 71, 161));

        panel.add(label, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}