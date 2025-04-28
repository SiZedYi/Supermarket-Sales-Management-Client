package org.example.ui;

import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.model.User;
import org.example.model.Customer;
import org.example.rmi.SupermarketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private SupermarketService service;
    private JTable invoiceTable;
    private DefaultTableModel invoiceTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JLabel lblCustomer;
    private JLabel lblOrderer;
    private JLabel lblOrderDate;
    private List<Invoice> invoiceList;

    public InvoiceManagementUI(SupermarketService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // Invoice list panel
        JPanel invoicePanel = new JPanel(new BorderLayout(5,5));
        invoiceTableModel = new DefaultTableModel(new String[]{"Invoice ID","Customer","Orderer","Date"},0);
        invoiceTable = new JTable(invoiceTableModel);
        invoicePanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // Panel chứa 2 nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnViewDetail = new JButton("Detail");
        JButton btnRefresh = new JButton("Refresh");
        buttonPanel.add(btnViewDetail);
        buttonPanel.add(btnRefresh);
        invoicePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Detail panel
        JPanel detailPanel = new JPanel(new BorderLayout(5,5));
        JPanel metaPanel = new JPanel(new GridLayout(3,1));
        lblCustomer = new JLabel("Customer: ");
        lblOrderer  = new JLabel("Orderer: ");
        lblOrderDate= new JLabel("Order Date: ");
        lblCustomer.setFont(lblCustomer.getFont().deriveFont(Font.BOLD));
        lblOrderer.setFont(lblOrderer.getFont().deriveFont(Font.BOLD));
        lblOrderDate.setFont(lblOrderDate.getFont().deriveFont(Font.BOLD));
        metaPanel.add(lblCustomer);
        metaPanel.add(lblOrderer);
        metaPanel.add(lblOrderDate);
        detailPanel.add(metaPanel, BorderLayout.NORTH);

        detailTableModel = new DefaultTableModel(new String[]{"Product","Qty","Unit Price","Discount","Total"},0);
        detailTable = new JTable(detailTableModel);
        detailPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // PDF button
        JButton btnPdf = new JButton("Generate PDF");
        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pdfPanel.add(btnPdf);
        detailPanel.add(pdfPanel, BorderLayout.SOUTH);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, invoicePanel, detailPanel);
        split.setDividerLocation(400);
        add(split, BorderLayout.CENTER);

        // Load invoices
        loadInvoices();

        // Button actions
        btnViewDetail.addActionListener((ActionEvent e) -> viewInvoiceDetails());
        btnRefresh.addActionListener((ActionEvent e) -> loadInvoices());
        btnPdf.addActionListener((ActionEvent e) -> generatePdfForSelected());
    }

    private void loadInvoices() {
        try {
            invoiceTableModel.setRowCount(0);
            invoiceList = service.getAllInvoices();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for (Invoice inv : invoiceList) {
                Customer c = inv.getCustomer();
                User u = inv.getUser();
                invoiceTableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        c != null ? c.getContactName() : "",
                        u != null ? u.getHoTen() : "",
                        inv.getOrderDate() != null ? df.format(inv.getOrderDate()) : ""
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoices.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInvoiceDetails() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice first.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        Invoice inv = invoiceList.get(row);
        lblCustomer.setText("Customer: " + (inv.getCustomer() != null ? inv.getCustomer().getContactName() : ""));
        lblOrderer.setText("Orderer: " + (inv.getUser() != null ? inv.getUser().getHoTen() : ""));
        lblOrderDate.setText("Order Date: " + new SimpleDateFormat("yyyy-MM-dd").format(inv.getOrderDate()));

        try {
            detailTableModel.setRowCount(0);
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            for (InvoiceDetail d : details) {
                double lineTotal = d.getQuantity() * d.getUnitPrice() - (d.getDiscount() != null ? d.getDiscount() : 0);
                detailTableModel.addRow(new Object[]{
                        d.getProduct().getProductName(),
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getDiscount() != null ? d.getDiscount() : 0,
                        String.format("%.2f", lineTotal)
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load invoice details.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }


    private void generatePdfForSelected() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice first.", "Warning", JOptionPane.WARNING_MESSAGE);
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

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableTopY = yStart - 100; // cách header 100px
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float rowHeight = 20;
            float cellMargin = 5;

            contentStream.setFont(font, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yStart);

            contentStream.showText("Invoice ID: " + inv.getInvoiceId());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Customer: " + (inv.getCustomer() != null ? inv.getCustomer().getContactName() : ""));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Orderer: " + (inv.getUser() != null ? inv.getUser().getHoTen() : ""));
            contentStream.newLineAtOffset(0, -20);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            contentStream.showText("Order Date: " + (inv.getOrderDate() != null ? df.format(inv.getOrderDate()) : ""));
            contentStream.endText();

            // Vẽ header bảng
            String[] headers = {"Product", "Qty", "Unit Price", "Discount", "Total"};
            float[] colWidths = {tableWidth * 0.4f, tableWidth * 0.1f, tableWidth * 0.15f, tableWidth * 0.15f, tableWidth * 0.2f};

            // Draw table header background
            contentStream.setNonStrokingColor(200, 200, 200); // light gray
            contentStream.addRect(margin, tableTopY - rowHeight, tableWidth, rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(0, 0, 0); // reset to black

            float nextX = margin;
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + cellMargin, tableTopY - 15);
            for (int i = 0; i < headers.length; i++) {
                contentStream.showText(headers[i]);
                contentStream.newLineAtOffset(colWidths[i], 0);
            }
            contentStream.endText();

            // Load chi tiết hóa đơn
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            float nextY = tableTopY - rowHeight;
            double grandTotal = 0;

            for (InvoiceDetail d : details) {
                nextY -= rowHeight;
                if (nextY < margin) {
                    // Thêm trang mới nếu hết trang
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    nextY = page.getMediaBox().getHeight() - margin;
                }

                double lineTotal = d.getQuantity() * d.getUnitPrice() - (d.getDiscount() != null ? d.getDiscount() : 0);
                grandTotal += lineTotal;

                String[] data = {
                        d.getProduct().getProductName(),
                        String.valueOf(d.getQuantity()),
                        String.format("%.2f", d.getUnitPrice()),
                        String.format("%.2f", d.getDiscount() != null ? d.getDiscount() : 0),
                        String.format("%.2f", lineTotal)
                };

                contentStream.beginText();
                contentStream.setFont(font, 11);
                contentStream.newLineAtOffset(margin + cellMargin, nextY + 5);
                nextX = margin + cellMargin;
                for (int i = 0; i < data.length; i++) {
                    contentStream.showText(data[i]);
                    nextX += colWidths[i];
                    contentStream.newLineAtOffset(colWidths[i], 0);
                }
                contentStream.endText();
            }

            // Vẽ Grand Total
            nextY -= rowHeight + 10;
            contentStream.beginText();
            contentStream.setFont(font, 13);
            contentStream.newLineAtOffset(margin + tableWidth - 150, nextY + 5);
            contentStream.showText("Grand Total: " + String.format("%.2f", grandTotal));
            contentStream.endText();

            contentStream.close();

            document.save(file);
            JOptionPane.showMessageDialog(this, "PDF generated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate PDF.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
