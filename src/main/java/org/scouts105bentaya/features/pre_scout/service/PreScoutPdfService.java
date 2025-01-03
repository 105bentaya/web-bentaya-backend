package org.scouts105bentaya.features.pre_scout.service;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.pre_scout.PreScoutUtils;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.shared.util.PdfUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PreScoutPdfService {
    public ByteArrayDataSource generatePreScoutPDF(PreScout preScout) {
        PdfPTable pdfPTable = new PdfPTable(12);

        String title = "FICHA DE INCORPORACIÓN A LISTA DE ESPERA - PREINSCRIPCIÓN";
        PdfUtils.createDoubleTitleHeader(pdfPTable, title);
        this.createPreScoutBody(preScout, pdfPTable);

        ByteArrayDataSource dataSource = PdfUtils.generatePdfByteArrayDataSourceFromPdfPTable(pdfPTable);
        dataSource.setName(generatePdfName(preScout));

        return dataSource;
    }

    private String generatePdfName(PreScout preScout) {
        String preScoutName = PdfUtils.replaceSeparationWithUnderScore("%s_%s".formatted(preScout.getName(), preScout.getSurname()));
        return "PREINSCRIPCIÓN_%s_SCOUTS_105_BENTAYA.pdf".formatted(preScoutName);
    }

    private void createPreScoutBody(PreScout preScout, PdfPTable table) {
        PdfCreator creator = new PdfCreator(table);
        int formYear = Integer.parseInt(preScout.getInscriptionYear());

        creator.addCell("Nombre y apellidos del niño/a o joven:", "%s %s".formatted(preScout.getName(), preScout.getSurname()), 8);
        creator.addCell("Género:", preScout.getGender(), 4);
        creator.addCell("Fecha de nacimiento:", preScout.getBirthday(), 3);
        creator.addCell(String.format("Edad a 31/12/%d:", formYear - 1), preScout.getAge(), 3);
        creator.addCell("DNI o NIE:", preScout.getDni(), 3);
        creator.addCell("Talla:", preScout.getSize(), 3);
        creator.addCell("Ha estado antes en el grupo:", preScout.isHasBeenInGroup() ? "SI" : "NO", preScout.isHasBeenInGroup() ? 4 : 12);

        if (preScout.isHasBeenInGroup()) creator.addCell("Año aproximado y unidad:", preScout.getYearAndSection(), 8);

        creator.addCell("Datos médicos relevantes:", preScout.getMedicalData(), 12, 70);
        creator.addCell("Nombre de la persona de contacto:", preScout.getParentsName(), 4);
        creator.addCell("Apellidos:", preScout.getParentsSurname(), 4);
        creator.addCell("Parentesco:", preScout.getRelationship(), 4);
        creator.addCell("Correo de contacto:", preScout.getEmail(), 8);
        creator.addCell("Teléfono de contacto:", preScout.getPhone(), 4);
        creator.addCell("Breve comentario sobre la motivación para su incorporación a la asociación:", preScout.getComment(), 12, 70);
        creator.addCell("El bloque de prioridad correspondiente es:", PreScoutUtils.getPriority(preScout.getPriority(), formYear), 12);

        if (PreScoutUtils.hasPriority(preScout.getPriority()) && preScout.getPriorityInfo() != null) {
            creator.addCell("Información sobre la prioridad:", preScout.getPriorityInfo(), 12);
        }

        creator.addCell("Fecha de Solicitud:", preScout.getCreationDate(), 12);
    }

    private record PdfCreator(PdfPTable table) {
        private void addCell(String title, String field, int colspan) {
            PdfPCell cell = PdfUtils.addParagraphToCell(title, field);
            cell.setColspan(colspan);
            table.addCell(cell);
        }

        public void addCell(String title, String field, int colspan, int minimumHeight) {
            PdfPCell cell = PdfUtils.addParagraphToCell(title, field);
            cell.setMinimumHeight(minimumHeight);
            cell.setColspan(colspan);
            table.addCell(cell);
        }
    }
}
