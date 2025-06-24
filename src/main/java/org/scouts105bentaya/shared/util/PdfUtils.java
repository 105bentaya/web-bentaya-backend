package org.scouts105bentaya.shared.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public final class PdfUtils {
    private static final Font HEAD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WebColors.getRGBColor("#F1E61F"));
    public static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    public static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final String LOGO_IMG = "img/logo.JPG";
    private static final Color HEADER_COLOR = WebColors.getRGBColor("#03569C");

    private PdfUtils() {
    }

    public static ByteArrayDataSource generatePdfByteArrayDataSourceFromPdfPTable(PdfPTable table) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PdfWriter instance = PdfWriter.getInstance(document, outputStream);

        document.open();
        instance.getInfo().put(new PdfName("PRUEBA NOMBRE"), new PdfString(Document.getVersion()));
        document.add(table);
        document.close();

        byte[] bytes = outputStream.toByteArray();
        return new ByteArrayDataSource(bytes, "application/pdf");
    }

    public static PdfPCell addParagraphToCell(String fieldTitle, String field) {
        PdfPCell cell = new PdfPCell();

        Paragraph pTittle = new Paragraph(fieldTitle, NORMAL_FONT);
        pTittle.setAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(pTittle);

        Paragraph pField = new Paragraph(field, BOLD_FONT);
        pField.setAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(pField);

        cell.setPaddingLeft(5);
        cell.setPaddingBottom(10);
        return cell;
    }

    public static void createDoubleTitleHeader(PdfPTable table, String secondTitle) {
        createTitleHeader(table);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(secondTitle, HEAD_FONT));
        cell.setColspan(12);
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setBorder(0);
        addAlignCenterCell(table, cell);
    }

    public static void createTitleHeader(PdfPTable table) {
        createBasicHeader(table);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("ASOCIACIÓN SCOUTS-EXPLORADORES BENTAYA", HEAD_FONT));
        cell.setColspan(12);
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setBorder(0);
        addAlignCenterCell(table, cell);
    }

    private static void createBasicHeader(PdfPTable table) {
        table.setWidthPercentage(100);
        try {
            Image img = Image.getInstance(new ClassPathResource(LOGO_IMG).getURL());
            PdfPCell cell = new PdfPCell(img, true);
            cell.setBorderWidthBottom(0);
            cell.setColspan(12);
            table.addCell(cell);
        } catch (IOException e) {
            log.error("Error whilst creating pdf header image: {}", e.getMessage());
        }
    }

    private static void addAlignCenterCell(PdfPTable table, PdfPCell cell) {
        cell.setFixedHeight(23);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public static String replaceSeparationWithUnderScore(String str) {
        return str.replaceAll("[, ]+", "_");
    }
}
