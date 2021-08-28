package app.kenavo.billapplication.utils;

import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFCreator {

    private static PdfFont helveticaFont;
    private static Color black = new DeviceRgb(88, 88, 90);

    static {
        try {
            helveticaFont = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePDF(Bill bill, List<Mission> missions) throws FileNotFoundException {
        String dest = "./" + bill.getNumber() +".pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        String title = "FACTURE " + bill.getNumber();
        addTitle(doc, title);

        addTable(doc, missions);

        addBillAnnotation(doc);

        doc.close();
    }

    private static void addTitle(Document document, String title) {
        Paragraph para = new Paragraph(title);
        para.setHorizontalAlignment(HorizontalAlignment.CENTER);
        para.setTextAlignment(TextAlignment.CENTER).setFont(helveticaFont).setFontSize(30).setBold();
        para.setMarginBottom(40);
        document.add(para);
    }

    private static void addTable(Document document, List<Mission> missions) {

        List<Float> amounts = new ArrayList<>();
        List<String> headers = new ArrayList<String>();
        headers.add("Description");
        headers.add("Quantity");
        headers.add("Price HT");
        headers.add("Amount HT");

        float [] pointColumnWidths = {300F, 70F, 70F, 70F};
        Table table = new Table(pointColumnWidths)
                .setMarginBottom(10)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        headers.forEach(header -> {
            Cell cell = new Cell();
            cell.add(header)
                    .setBackgroundColor(black)
                    .setFontColor(Color.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        });

        missions.forEach(mission -> {
            Float amount = mission.getQuantity() * mission.getPrice();
            amounts.add(amount);

            Paragraph description = new Paragraph(String.valueOf(mission.getDescription()));
            Paragraph quantity = new Paragraph(String.valueOf(mission.getQuantity()));
            Paragraph price = new Paragraph(mission.getPrice() + " €");
            Paragraph pAmount = new Paragraph(amount + " €");

            Cell cellDescription = new Cell();
            Cell cellQuantity = new Cell();
            cellQuantity.setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(5);
            Cell cellPrice = new Cell();
            cellPrice.setTextAlignment(TextAlignment.RIGHT)
                    .setPaddingRight(5);
            Cell cellAmount = new Cell();
            cellAmount.setTextAlignment(TextAlignment.RIGHT)
                    .setPaddingRight(5);

            cellDescription.add(description);
            cellQuantity.add(quantity);
            cellPrice.add(price);
            cellAmount.add(pAmount);


            table.addCell(cellDescription);
            table.addCell(cellQuantity);
            table.addCell(cellPrice);
            table.addCell(cellAmount);
        });

        final Float[] total = {0F};
        amounts.forEach(amount -> total[0] += amount);

        Paragraph totalAmount = new Paragraph("Total de la facture : " + total[0] + " €")
                .setBold()
                .setFontSize(15)
                .setUnderline()
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingBottom(20);

        document.add(table);
        document.add(totalAmount);

    }

    private static void addBillAnnotation(Document document) {
        Paragraph condition = new Paragraph("Conditions de paiement et mentions particulières :")
                .setFontSize(10)
                .setPaddingBottom(0)
                .setMarginBottom(0);
        Paragraph law = new Paragraph("TVA non applicable, article 293B du code général des impôts")
                .setFontSize(10)
                .setPaddingBottom(0)
                .setMarginBottom(0)
                .setItalic();
        document.add(condition);
        document.add(law);
    }
}
