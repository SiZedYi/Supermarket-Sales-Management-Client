package org.example.ui;

import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.model.User;
import org.example.model.Customer;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class InvoiceManagementUI extends JPanel {
    // Định nghĩa các màu sắc chủ đạo cho giao diện
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210); // Màu xanh dương đậm
    private static final Color SECONDARY_COLOR = new Color(63, 81, 181); // Màu xanh dương nhạt
    private static final Color ACCENT_COLOR = new Color(255, 152, 0); // Màu cam
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Màu xám nhạt
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // Màu đen nhạt
    private static final Color TABLE_HEADER_COLOR = new Color(225, 232, 240); // Màu xanh nhạt cho header bảng
    private static final Color TABLE_ROW_ALT_COLOR = new Color(240, 248, 255); // Màu xanh nhạt cho dòng chẵn

    private SupermarketService service;
    private JTable invoiceTable;
    private DefaultTableModel invoiceTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JLabel lblCustomer;
    private JLabel lblOrderer;
    private JLabel lblOrderDate;
    private JLabel lblTotal;
    private List<Invoice> invoiceList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public InvoiceManagementUI(SupermarketService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tạo Panel chứa header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panel chứa split pane và nội dung chính
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Invoice list panel
        JPanel invoicePanel = createInvoicePanel();

        // Detail panel
        JPanel detailPanel = createDetailPanel();

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, invoicePanel, detailPanel);
        split.setDividerLocation(450);
        split.setOneTouchExpandable(true);
        split.setDividerSize(8);
        split.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(split, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        // Load invoices
        loadInvoices();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Invoice Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createInvoicePanel() {
        JPanel invoicePanel = new JPanel(new BorderLayout(0, 10));
        invoicePanel.setBackground(BACKGROUND_COLOR);

        // Tạo tiêu đề cho panel
        JLabel invoiceListLabel = new JLabel("Invoice List");
        invoiceListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        invoiceListLabel.setForeground(PRIMARY_COLOR);
        invoiceListLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        invoicePanel.add(invoiceListLabel, BorderLayout.NORTH);

        // Tạo model và table
        invoiceTableModel = new DefaultTableModel(new String[]{"ID", "Customer", "Orderer", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Làm cho bảng không thể chỉnh sửa
            }
        };

        invoiceTable = new JTable(invoiceTableModel);
        styleTable(invoiceTable);

        // Set column widths
        invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        invoiceTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        invoiceTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        invoicePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa 2 nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton btnViewDetail = createStyledButton("View Detail", PRIMARY_COLOR);
        JButton btnRefresh = createStyledButton("Refresh", SECONDARY_COLOR);

        buttonPanel.add(btnViewDetail);
        buttonPanel.add(btnRefresh);
        invoicePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        btnViewDetail.addActionListener((ActionEvent e) -> viewInvoiceDetails());
        btnRefresh.addActionListener((ActionEvent e) -> loadInvoices());

        return invoicePanel;
    }

    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout(0, 10));
        detailPanel.setBackground(BACKGROUND_COLOR);

        // Tiêu đề
        JLabel detailLabel = new JLabel("Invoice Details");
        detailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailLabel.setForeground(PRIMARY_COLOR);
        detailLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        detailPanel.add(detailLabel, BorderLayout.NORTH);

        // Panel chứa thông tin hóa đơn
        JPanel metaPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        metaPanel.setBackground(Color.WHITE);
        metaPanel.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        Font labelFont = new Font("Arial", Font.BOLD, 13);

        lblCustomer = new JLabel("Customer: ");
        lblOrderer = new JLabel("Orderer: ");
        lblOrderDate = new JLabel("Order Date: ");
        lblTotal = new JLabel("Total Amount: ");

        lblCustomer.setFont(labelFont);
        lblOrderer.setFont(labelFont);
        lblOrderDate.setFont(labelFont);
        lblTotal.setFont(labelFont);

        lblCustomer.setForeground(TEXT_COLOR);
        lblOrderer.setForeground(TEXT_COLOR);
        lblOrderDate.setForeground(TEXT_COLOR);
        lblTotal.setForeground(ACCENT_COLOR);

        metaPanel.add(lblCustomer);
        metaPanel.add(lblOrderer);
        metaPanel.add(lblOrderDate);
        metaPanel.add(lblTotal);

        detailPanel.add(metaPanel, BorderLayout.NORTH);

        // Table for invoice details
        detailTableModel = new DefaultTableModel(
                new String[]{"Product", "Qty", "Unit Price", "Discount", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Làm cho bảng không thể chỉnh sửa
            }
        };

        detailTable = new JTable(detailTableModel);
        styleTable(detailTable);

        // Set column widths
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Create center renderer for numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Create right renderer for price columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        detailTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        detailTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        detailTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        detailScroll.getViewport().setBackground(Color.WHITE);
        detailPanel.add(detailScroll, BorderLayout.CENTER);

        // PDF button
        JButton btnPdf = createStyledButton("Generate PDF", ACCENT_COLOR);
        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pdfPanel.setBackground(BACKGROUND_COLOR);
        pdfPanel.add(btnPdf);
        detailPanel.add(pdfPanel, BorderLayout.SOUTH);

        // Button action
        btnPdf.addActionListener((ActionEvent e) -> generatePdfForSelected());

        return detailPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel footerLabel = new JLabel("© 2025 Supermarket Management System");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerPanel.add(footerLabel, BorderLayout.CENTER);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(PRIMARY_COLOR);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(new LineBorder(PRIMARY_COLOR, 1));

        // Style cells
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT_COLOR);
                    comp.setForeground(TEXT_COLOR);
                }

                return comp;
            }
        });
    }

    private void loadInvoices() {
        try {
            invoiceTableModel.setRowCount(0);
            invoiceList = service.getAllInvoices();

            for (Invoice inv : invoiceList) {
                Customer c = inv.getCustomer();
                User u = inv.getUser();
                invoiceTableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        c != null ? c.getContactName() : "",
                        u != null ? u.getHoTen() : "",
                        inv.getOrderDate() != null ? dateFormat.format(inv.getOrderDate()) : ""
                });
            }

            // Reset detail panel
            lblCustomer.setText("Customer: ");
            lblOrderer.setText("Orderer: ");
            lblOrderDate.setText("Order Date: ");
            lblTotal.setText("Total Amount: ");
            detailTableModel.setRowCount(0);

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to load invoices.", ex.getMessage());
        }
    }

    private void viewInvoiceDetails() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            showWarningDialog("Please select an invoice first.");
            return;
        }

        Invoice inv = invoiceList.get(row);
        lblCustomer.setText("Customer: " + (inv.getCustomer() != null ? inv.getCustomer().getContactName() : ""));
        lblOrderer.setText("Orderer: " + (inv.getUser() != null ? inv.getUser().getHoTen() : ""));
        lblOrderDate.setText("Order Date: " + (inv.getOrderDate() != null ? dateFormat.format(inv.getOrderDate()) : ""));

        try {
            detailTableModel.setRowCount(0);
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            double grandTotal = 0;

            for (InvoiceDetail d : details) {
                double lineTotal = d.getQuantity() * d.getUnitPrice() - (d.getDiscount() != null ? d.getDiscount() : 0);
                grandTotal += lineTotal;

                detailTableModel.addRow(new Object[]{
                        d.getProduct().getProductName(),
                        d.getQuantity(),
                        String.format("%,.2f", d.getUnitPrice()),
                        String.format("%,.2f", d.getDiscount() != null ? d.getDiscount() : 0),
                        String.format("%,.2f", lineTotal)
                });
            }

            lblTotal.setText("Total Amount: " + String.format("%,.2f", grandTotal));

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to load invoice details.", ex.getMessage());
        }
    }

    private void generatePdfForSelected() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            showWarningDialog("Please select an invoice first.");
            return;
        }

        Invoice inv = invoiceList.get(row);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Invoice_" + inv.getInvoiceId() + ".pdf"));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType0Font font = PDType0Font.load(document, new File("fonts/arial-unicode-ms.ttf"));
            PDType0Font boldFont = PDType0Font.load(document, new File("fonts/arial-unicode-ms.ttf"));

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableTopY = yStart - 160; // Tăng khoảng cách để có thêm chỗ cho header
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float rowHeight = 25;
            float cellMargin = 5;

            // Add a title
            contentStream.setNonStrokingColor(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue());
            contentStream.setFont(boldFont, 22);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yStart);
            contentStream.showText("INVOICE");
            contentStream.endText();

            // Add company logo (rectangle placeholder)
            contentStream.addRect(page.getMediaBox().getWidth() - margin - 100, yStart - 30, 100, 40);
            contentStream.fill();

            // Company info
            contentStream.setNonStrokingColor(0, 0, 0); // Black
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yStart - 40);
            contentStream.showText("Your Supermarket Name");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("123 Business Street");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("City, State, ZIP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Phone: (123) 456-7890");
            contentStream.endText();

            // Invoice info
            float infoX = page.getMediaBox().getWidth() - margin - 200;
            contentStream.beginText();
            contentStream.newLineAtOffset(infoX, yStart - 40);
            contentStream.showText("Invoice No: " + inv.getInvoiceId());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Date: " + (inv.getOrderDate() != null ? dateFormat.format(inv.getOrderDate()) : ""));
            contentStream.endText();

            // Separator line
            contentStream.setLineWidth(1f);
            contentStream.setStrokingColor(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue());
            contentStream.moveTo(margin, yStart - 80);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yStart - 80);
            contentStream.stroke();

            // Customer info
            contentStream.setNonStrokingColor(0, 0, 0); // Black
            contentStream.beginText();
            contentStream.setFont(boldFont, 14);
            contentStream.newLineAtOffset(margin, yStart - 100);
            contentStream.showText("Bill To:");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(margin, yStart - 120);
            contentStream.showText("Customer: " + (inv.getCustomer() != null ? inv.getCustomer().getContactName() : ""));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Orderer: " + (inv.getUser() != null ? inv.getUser().getHoTen() : ""));
            contentStream.endText();

            // Table header background
            contentStream.setNonStrokingColor(TABLE_HEADER_COLOR.getRed(), TABLE_HEADER_COLOR.getGreen(), TABLE_HEADER_COLOR.getBlue());
            contentStream.addRect(margin, tableTopY - rowHeight, tableWidth, rowHeight);
            contentStream.fill();

            // Table border
            contentStream.setStrokingColor(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue());
            contentStream.addRect(margin, tableTopY - rowHeight, tableWidth, rowHeight);
            contentStream.stroke();

            // Table headers
            String[] headers = {"Product", "Qty", "Unit Price", "Discount", "Total"};
            float[] colWidths = {tableWidth * 0.4f, tableWidth * 0.1f, tableWidth * 0.15f, tableWidth * 0.15f, tableWidth * 0.2f};

            contentStream.setNonStrokingColor(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue());
            contentStream.setFont(boldFont, 12);

            float nextX = margin;
            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                if (i == 0) {
                    contentStream.newLineAtOffset(nextX + cellMargin, tableTopY - 15);
                } else if (i == headers.length - 1) {
                    // Right align last column
                    float textWidth = boldFont.getStringWidth(headers[i]) / 1000 * 12;
                    contentStream.newLineAtOffset(nextX + colWidths[i] - textWidth - cellMargin, tableTopY - 15);
                } else {
                    // Center align middle columns
                    float textWidth = boldFont.getStringWidth(headers[i]) / 1000 * 12;
                    contentStream.newLineAtOffset(nextX + (colWidths[i] - textWidth) / 2, tableTopY - 15);
                }
                contentStream.showText(headers[i]);
                contentStream.endText();
                nextX += colWidths[i];
            }

            // Table rows
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            float nextY = tableTopY - rowHeight;
            double grandTotal = 0;
            boolean isAlternate = false;

            for (InvoiceDetail d : details) {
                // Alternating row colors
                if (isAlternate) {
                    contentStream.setNonStrokingColor(TABLE_ROW_ALT_COLOR.getRed(), TABLE_ROW_ALT_COLOR.getGreen(), TABLE_ROW_ALT_COLOR.getBlue());
                    contentStream.addRect(margin, nextY - rowHeight, tableWidth, rowHeight);
                    contentStream.fill();
                }
                isAlternate = !isAlternate;

                // Draw cell borders
                contentStream.setStrokingColor(200, 200, 200);
                contentStream.addRect(margin, nextY - rowHeight, tableWidth, rowHeight);
                contentStream.stroke();

                double lineTotal = d.getQuantity() * d.getUnitPrice() - (d.getDiscount() != null ? d.getDiscount() : 0);
                grandTotal += lineTotal;

                String[] data = {
                        d.getProduct().getProductName(),
                        String.valueOf(d.getQuantity()),
                        String.format("%,.2f", d.getUnitPrice()),
                        String.format("%,.2f", d.getDiscount() != null ? d.getDiscount() : 0),
                        String.format("%,.2f", lineTotal)
                };

                contentStream.setNonStrokingColor(0, 0, 0); // Black
                contentStream.setFont(font, 11);

                nextX = margin;
                for (int i = 0; i < data.length; i++) {
                    contentStream.beginText();
                    if (i == 0) {
                        contentStream.newLineAtOffset(nextX + cellMargin, nextY - rowHeight + 10);
                    } else if (i == data.length - 1) {
                        // Right align last column
                        float textWidth = font.getStringWidth(data[i]) / 1000 * 11;
                        contentStream.newLineAtOffset(nextX + colWidths[i] - textWidth - cellMargin, nextY - rowHeight + 10);
                    } else {
                        // Center align middle columns
                        float textWidth = font.getStringWidth(data[i]) / 1000 * 11;
                        contentStream.newLineAtOffset(nextX + (colWidths[i] - textWidth) / 2, nextY - rowHeight + 10);
                    }
                    contentStream.showText(data[i]);
                    contentStream.endText();
                    nextX += colWidths[i];
                }

                nextY -= rowHeight;

                // Add new page if needed
                if (nextY - rowHeight < margin + 50) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    nextY = page.getMediaBox().getHeight() - margin;
                    tableTopY = nextY - 40;
                }
            }

            // Grand Total background
            contentStream.setNonStrokingColor(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue());
            contentStream.addRect(margin + tableWidth - colWidths[4], nextY - rowHeight, colWidths[4], rowHeight);
            contentStream.fill();

            // Grand Total text
            contentStream.setNonStrokingColor(255, 255, 255); // White
            contentStream.setFont(boldFont, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + tableWidth - colWidths[4] + cellMargin, nextY - rowHeight + 10);
            contentStream.showText("Grand Total: " + String.format("%,.2f", grandTotal));
            contentStream.endText();

            // Footer
            contentStream.setNonStrokingColor(0, 0, 0); // Black
            contentStream.setFont(font, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, margin - 20);
            contentStream.showText("Thank you for your business!");
            contentStream.endText();

            contentStream.close();
            document.save(file);

            showSuccessDialog("PDF generated successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to generate PDF.", ex.getMessage());
        }
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String message, String details) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel(message), BorderLayout.NORTH);

        if (details != null && !details.isEmpty()) {
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setRows(5);
            textArea.setColumns(40);
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        JOptionPane.showMessageDialog(this,
                panel,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}