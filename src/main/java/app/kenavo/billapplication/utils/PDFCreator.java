package app.kenavo.billapplication.utils;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.AccountService;
import app.kenavo.billapplication.services.AccountServiceImpl;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFCreator {

    static AccountService accountService = new AccountServiceImpl();
    static List<Account> accounts = accountService.getAllAccounts();
    static SettingService settingService = new SettingServiceImpl();
    static Setting setting = settingService.getSetting();

    private static PdfFont helveticaFont;
    private static Color black = new DeviceRgb(88, 88, 90);

    static {
        try {
            helveticaFont = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePDF(Bill bill, List<Mission> missions) throws IOException {
        String dest = setting.getDownloadPath() + "/" + bill.getNumber() +"_V" + bill.getVersionPDF() + ".pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        addOwnContactBlock(doc);
        addAccountContactBlock(doc, bill.getAccountId());

        String title = "FACTURE " + bill.getNumber();
        addTitle(doc, title);

        addTable(doc, missions);

        addBillAnnotation(doc);

        addFooter(doc, pdfDoc);
        doc.close();
    }

    private static void addTitle(Document document, String title) {
        Paragraph para = new Paragraph(title);
        para.setHorizontalAlignment(HorizontalAlignment.CENTER);
        para.setTextAlignment(TextAlignment.CENTER).setFont(helveticaFont).setFontSize(30).setBold();
        para.setMarginBottom(40);
        para.setMarginTop(40);
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

    private static void addOwnContactBlock(Document document) {
        Paragraph name = new Paragraph(setting.getCompanyName())
                .setTextAlignment(TextAlignment.LEFT);
        String address = setting.getAddress();
        String[] arrAddress = address.split(",");

        Paragraph street = new Paragraph(arrAddress[0])
                .setTextAlignment(TextAlignment.LEFT);
        Paragraph city = new Paragraph(arrAddress[1])
                .setTextAlignment(TextAlignment.LEFT);
        Paragraph siren = new Paragraph(setting.getSiret())
                .setTextAlignment(TextAlignment.LEFT);

        document.add(name);
        document.add(street);
        document.add(city);
        document.add(siren);
    }

    private static void addAccountContactBlock(Document document, String accountId) {

        Account account = accountService.getAccountById(accounts, accountId);

        Paragraph name = new Paragraph(account.getName())
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
        String address = account.getAddress();
        String[] arrAddress = address.split(",");

        Paragraph street = new Paragraph(arrAddress[0])
                .setTextAlignment(TextAlignment.RIGHT);
        Paragraph city = new Paragraph(arrAddress[1])
                .setTextAlignment(TextAlignment.RIGHT);

        document.add(name);
        document.add(street);
        document.add(city);
    }

    private static void addFooter(Document document, PdfDocument pdfDocument) throws IOException {


        Paragraph header1 = new Paragraph(setting.getCompanyName() + " - " + setting.getAddress())
                .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE))
                .setFontSize(11);
        Paragraph header2 = new Paragraph(setting.getEmail() + " - " + setting.getPhone() + " - " + setting.getSiret())
                .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE))
                .setFontSize(11);

        Rectangle pageSize = pdfDocument.getPage(1).getPageSize();
        float x = pageSize.getWidth() / 2;
        float y = pageSize.getBottom() + 15;


        document.showTextAligned(header1, x, y, 1, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(header2, x, 0, 1, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
    }
}
