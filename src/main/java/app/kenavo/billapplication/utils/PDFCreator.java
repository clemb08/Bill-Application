package app.kenavo.billapplication.utils;

import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import com.itextpdf.io.font.FontConstants;
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
        Paragraph para = new Paragraph();
        List<String> headers = new ArrayList<String>();
        headers.add("Description");
        headers.add("Quantity");
        headers.add("Price");
        headers.add("Amount");

        Table table = new Table(4);
        headers.forEach(header -> {
            table.addCell(header);
        });

        missions.forEach(mission -> {
            String amount = String.valueOf(mission.getQuantity() * mission.getPrice());
            Paragraph description = new Paragraph(String.valueOf(mission.getDescription()));
            Paragraph quantity = new Paragraph(String.valueOf(mission.getQuantity()));
            Paragraph price = new Paragraph(String.valueOf(mission.getPrice()));
            Paragraph pAmount = new Paragraph(amount);

            Cell cellDescription = new Cell();
            Cell cellQuantity = new Cell();
            Cell cellPrice = new Cell();
            Cell cellAmount = new Cell();
            cellDescription.add(description);
            cellQuantity.add(quantity);
            cellPrice.add(price);
            cellAmount.add(pAmount);


            table.addCell(cellDescription);
            table.addCell(cellQuantity);
            table.addCell(cellPrice);
            table.addCell(cellAmount);
        });

        document.add(table);

    }

    private static void addBillAnnotation(Document document) {
        Paragraph condition = new Paragraph("Conditions de paiement et mentions particulières :");
        Paragraph law = new Paragraph("TVA non applicable, article 293B du code général des impôts");
        document.add(condition);
        document.add(law);
    }
}
