package org.scouts105bentaya.shared.service;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.complaint.Complaint;
import org.scouts105bentaya.features.pre_scouter.PreScouter;
import org.scouts105bentaya.shared.util.PdfUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PdfService {
    public ByteArrayDataSource generatePreScouterPDF(PreScouter preScouter) {
        PdfPTable pdfPTable = new PdfPTable(12);

        String title = "FICHA DE INCORPORACIÓN DE VOLUNTARIADO";
        PdfUtils.createDoubleTitleHeader(pdfPTable, title);
        this.createPreScouterBody(preScouter, pdfPTable);

        ByteArrayDataSource dataSource = PdfUtils.generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);
        dataSource.setName(
            "Preinscripción_Voluntariado_" +
            PdfUtils.replaceSeparationWithUnderScore(preScouter.getName()) + "_" +
            PdfUtils.replaceSeparationWithUnderScore(preScouter.getSurname()) + "_" +
            preScouter.getCreationDate() + ".pdf"
        );
        return dataSource;
    }

    public ByteArrayDataSource generateComplaintPDF(Complaint complaint) {
        PdfPTable pdfPTable = new PdfPTable(8);

        PdfUtils.createDoubleTitleHeader(pdfPTable, "ANEXO DE DENUNCIA RECIBIDA");
        this.createComplaintBody(complaint, pdfPTable);

        ByteArrayDataSource dataSource = PdfUtils.generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);

        dataSource.setName(
            "DENUNCIA " + complaint.getCategory() + " " +
            complaint.getType() + ".pdf");
        return dataSource;
    }

    private void createPreScouterBody(PreScouter preScouter, PdfPTable table) {
        PdfPCell cell = PdfUtils.addParagraphToCell(
            "Nombre y apellidos:",
            preScouter.getName().toUpperCase() + ", " + preScouter.getSurname().toUpperCase()
        );
        cell.setColspan(6);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Fecha de nacimiento:",
            preScouter.getBirthday());
        cell.setColspan(6);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Género:",
            preScouter.getGender().toUpperCase());
        cell.setColspan(2);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Teléfono de contacto:",
            preScouter.getPhone());
        cell.setColspan(4);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Correo de contacto:",
            preScouter.getEmail());
        cell.setColspan(8);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Breve comentario sobre la motivación para su incorporación a la asociación:",
            preScouter.getComment());
        cell.setColspan(12);
        cell.setMinimumHeight(70);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Fecha de Solicitud:",
            preScouter.getCreationDate());
        cell.setColspan(12);
        table.addCell(cell);
    }

    private void createComplaintBody(Complaint complaint, PdfPTable table) {
        PdfPCell cell = PdfUtils.addParagraphToCell(
            "Categoría: ",
            complaint.getCategory().toUpperCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Tipo: ",
            complaint.getType().toUpperCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Nombre y apellidos: ",
            complaint.getName().toUpperCase());
        cell.setColspan(8);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Tlfno. contacto: ",
            complaint.getPhone());
        cell.setColspan(4);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Correo de contacto: ",
            complaint.getEmail().toLowerCase());
        cell.setColspan(4);
        table.addCell(cell);

        cell = PdfUtils.addParagraphToCell(
            "Descripción de la denuncia: ",
            complaint.getText());
        cell.setColspan(8);
        cell.setMinimumHeight(70);
        table.addCell(cell);
    }
}
