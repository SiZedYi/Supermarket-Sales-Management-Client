package org.example.ui;

import org.example.model.Invoice;
import org.example.model.InvoiceDetail;
import org.example.model.User;
import org.example.model.Customer;
import org.example.rmi.SupermarketService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

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
            JOptionPane.showMessageDialog(this, "Select an invoice to export.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Invoice inv = invoiceList.get(row);
        try {
            List<InvoiceDetail> details = service.getInvoiceDetails(inv.getInvoiceId());
            File file = new File("Invoice_" + inv.getInvoiceId() + ".pdf");
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Load UTF-8 font
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED, true);

            // Title
            Paragraph title = new Paragraph("Invoice To")
                    .setFont(font)
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(title);

            doc.add(new Paragraph("Invoice ID: " + inv.getInvoiceId()).setFont(font).setBold());
            doc.add(new Paragraph("Customer: " + inv.getCustomer().getContactName()).setFont(font).setBold());
            doc.add(new Paragraph("Orderer: " + inv.getUser().getHoTen()).setFont(font).setBold());
            doc.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(inv.getOrderDate())).setFont(font).setBold());
            doc.add(new Paragraph(" "));

            Table table = new Table(new float[]{4, 2, 2, 2, 2});
            table.addHeaderCell(new Cell().add(new Paragraph("Product").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Qty").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Discount").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(font).setBold()));

            double grandTotal = 0;
            for (InvoiceDetail d : details) {
                double lineTotal = d.getQuantity() * d.getUnitPrice() - (d.getDiscount() != null ? d.getDiscount() : 0);
                grandTotal += lineTotal;
                table.addCell(new Cell().add(new Paragraph(d.getProduct().getProductName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(d.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(d.getUnitPrice())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(d.getDiscount() != null ? d.getDiscount() : 0)).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", lineTotal)).setFont(font)));
            }

            doc.add(table);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Grand Total: " + String.format("%.2f", grandTotal)).setFont(font).setBold());
            doc.add(new Paragraph("Billing only available today").setFont(font).setItalic().setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            doc.close();
            JOptionPane.showMessageDialog(this, "PDF generated: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "PDF generation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
