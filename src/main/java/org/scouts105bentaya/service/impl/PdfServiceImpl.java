package org.scouts105bentaya.service.impl;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.entity.Complaint;
import org.scouts105bentaya.entity.PreScout;
import org.scouts105bentaya.entity.PreScouter;
import org.scouts105bentaya.service.PdfService;
import org.scouts105bentaya.util.FormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.scouts105bentaya.util.FormUtils.getPriority;
import static org.scouts105bentaya.util.FormUtils.hasPriority;

@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    private final Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WebColors.getRGBColor("#F1E61F"));
    private final Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final String LOGO_IMG = "img/logo.JPG";
    private static final Color HEADER_COLOR = WebColors.getRGBColor("#03569C");

    public PdfServiceImpl() {
    }

    public ByteArrayDataSource generatePreScoutPDF(PreScout preScout) {
        PdfPTable pdfPTable = new PdfPTable(12);
        int formYear = Integer.parseInt(preScout.getInscriptionYear());

        String title = "FICHA DE INCORPORACIÓN A LISTA DE ESPERA - PREINSCRIPCIÓN";
        this.createDoubleTitleHeader(pdfPTable, title);
        this.createPreScoutBody(preScout, pdfPTable, formYear);

        ByteArrayDataSource dataSource = generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);
        dataSource.setName(
                FormUtils.getGroup(preScout.getBirthday(), false, formYear - 1) + "_"
                        + removeSpecialCharacters(preScout.getName()) + "_"
                        + removeSpecialCharacters(preScout.getSurname()) + "_"
                        + preScout.getCreationDate() + ".pdf"
        );

        return dataSource;
    }

    public ByteArrayDataSource generatePreScouterPDF(PreScouter preScouter) {
        PdfPTable pdfPTable = new PdfPTable(12);

        String title = "FICHA DE INCORPORACIÓN DE VOLUNTARIADO";
        this.createDoubleTitleHeader(pdfPTable, title);
        this.createPreScouterBody(preScouter, pdfPTable);

        ByteArrayDataSource dataSource = generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);
        dataSource.setName(
                "Preinscripcion_Voluntariado_" +
                        removeSpecialCharacters(preScouter.getName()) + "_" +
                        removeSpecialCharacters(preScouter.getSurname()) + "_" +
                        preScouter.getCreationDate() + ".pdf"
        );
        return dataSource;
    }

    public ByteArrayDataSource generateComplaintPDF(Complaint complaint) {
        PdfPTable pdfPTable = new PdfPTable(8);

        this.createDoubleTitleHeader(pdfPTable, "ANEXO DE DENUNCIA RECIBIDA");
        this.createComplaintBody(complaint, pdfPTable);

        ByteArrayDataSource dataSource = generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);

        dataSource.setName(
                "DENUNCIA " + complaint.getCategory() + " " +
                        complaint.getType() + ".pdf");
        return dataSource;
    }

    private ByteArrayDataSource generatePdfByteArrayDataSourceFromPdfPTable(PdfPTable table) {
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


    private void createBasicHeader(PdfPTable table) {
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

    private void createDoubleTitleHeader(PdfPTable table, String secondTitle) {
        createBasicHeader(table);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("ASOCIACIÓN SCOUTS-EXPLORADORES BENTAYA", headFont));
        cell.setColspan(12);
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setBorder(0);
        this.addAlignCenterCell(table, cell);

        cell = new PdfPCell(new Phrase(secondTitle, headFont));
        cell.setColspan(12);
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setBorder(0);
        this.addAlignCenterCell(table, cell);
    }

    private void createPreScoutBody(PreScout preScout, PdfPTable table, int formYear) {
        PdfPCell cell = this.addParagraphToCell(
                "Nombre y apellidos del niño/a o joven: ",
                preScout.getName() + " " + preScout.getSurname());
        cell.setColspan(8);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Género: ",
                preScout.getGender());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Fecha de nacimiento: ",
                preScout.getBirthday());
        cell.setColspan(3);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                String.format("Edad a 31/12/%d: ", formYear - 1),
                preScout.getAge());
        cell.setColspan(3);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "DNI o NIE: ",
                preScout.getDni());
        cell.setColspan(3);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Talla: ",
                preScout.getSize());
        cell.setColspan(3);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Ha estado antes en el grupo:",
                preScout.isHasBeenInGroup() ? "SI" : "NO");
        cell.setColspan(preScout.isHasBeenInGroup() ? 4 : 12);
        table.addCell(cell);

        if (preScout.isHasBeenInGroup()) {
            cell = this.addParagraphToCell(
                    "Año aproximado y unidad:",
                    preScout.getYearAndSection());
            cell.setColspan(8);
            table.addCell(cell);
        }

        cell = this.addParagraphToCell(
                "Datos médicos relevantes:",
                preScout.getMedicalData());
        cell.setMinimumHeight(70);
        cell.setColspan(12);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Nombre de la persona de contacto:",
                preScout.getParentsName());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Apellidos:",
                preScout.getParentsSurname());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Parentesco:",
                preScout.getRelationship());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Correo de contacto:",
                preScout.getEmail());
        cell.setColspan(8);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Teléfono de contacto:",
                preScout.getPhone());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Breve comentario sobre la motivación para su incorporación a la asociación:",
                preScout.getComment());
        cell.setColspan(12);
        cell.setMinimumHeight(70);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "El bloque de prioridad correspondiente es:",
                getPriority(preScout.getPriority(), formYear));
        cell.setColspan(12);
        table.addCell(cell);

        if (hasPriority(preScout.getPriority()) && preScout.getPriorityInfo() != null) {
            cell = this.addParagraphToCell(
                    "Información sobre la prioridad:",
                    preScout.getPriorityInfo());
            cell.setColspan(12);
            table.addCell(cell);
        }

        cell = this.addParagraphToCell(
                "Fecha de Solicitud:",
                preScout.getCreationDate());
        cell.setColspan(12);
        table.addCell(cell);
    }

    private void createPreScouterBody(PreScouter preScouter, PdfPTable table) {
        PdfPCell cell = this.addParagraphToCell(
                "Nombre y apellidos:",
                preScouter.getName().toUpperCase() + ", " + preScouter.getSurname().toUpperCase()
        );
        cell.setColspan(6);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Fecha de nacimiento:",
                preScouter.getBirthday());
        cell.setColspan(6);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Género:",
                preScouter.getGender().toUpperCase());
        cell.setColspan(2);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Teléfono de contacto:",
                preScouter.getPhone());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Correo de contacto:",
                preScouter.getEmail());
        cell.setColspan(8);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Breve comentario sobre la motivación para su incorporación a la asociación:",
                preScouter.getComment());
        cell.setColspan(12);
        cell.setMinimumHeight(70);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Fecha de Solicitud:",
                preScouter.getCreationDate());
        cell.setColspan(12);
        table.addCell(cell);
    }

    private void createComplaintBody(Complaint complaint, PdfPTable table) {
        PdfPCell cell = this.addParagraphToCell(
                "Categoría: ",
                complaint.getCategory().toUpperCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Tipo: ",
                complaint.getType().toUpperCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Nombre y apellidos: ",
                complaint.getName().toUpperCase());
        cell.setColspan(8);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Tlfno. contacto: ",
                complaint.getPhone());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Correo de contacto: ",
                complaint.getEmail().toLowerCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = this.addParagraphToCell(
                "Descripción de la denuncia: ",
                complaint.getText());
        cell.setColspan(8);
        cell.setMinimumHeight(70);
        table.addCell(cell);
    }

    private static String removeSpecialCharacters(String str) {
        return StringUtils.stripAccents(str).replaceAll(", ", "_").replaceAll("[, ]", "_");
    }

    private PdfPCell addParagraphToCell(String fieldTitle, String field) {
        PdfPCell cell = new PdfPCell();

        Paragraph pTittle = new Paragraph(fieldTitle, this.normalFont);
        pTittle.setAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(pTittle);

        Paragraph pField = new Paragraph(field, this.boldFont);
        pField.setAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(pField);

        cell.setPaddingLeft(5);
        cell.setPaddingBottom(10);
        return cell;
    }

    private void addAlignCenterCell(PdfPTable table, PdfPCell cell) {
        cell.setFixedHeight(23);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
