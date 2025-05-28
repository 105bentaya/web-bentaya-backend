package org.scouts105bentaya.features.jamboree_inscription;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.features.jamboree_inscription.dto.JamboreeForm;
import org.scouts105bentaya.features.jamboree_inscription.entity.JamboreeContact;
import org.scouts105bentaya.features.jamboree_inscription.entity.JamboreeInscription;
import org.scouts105bentaya.features.jamboree_inscription.entity.JamboreeLanguage;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class JamboreeInscriptionService {

    private final JamboreeInscriptionRepository jamboreeInscriptionRepository;
    private final ScoutRepository scoutRepository;

    public JamboreeInscriptionService(
        JamboreeInscriptionRepository jamboreeInscriptionRepository,
        ScoutRepository scoutRepository
    ) {
        this.jamboreeInscriptionRepository = jamboreeInscriptionRepository;
        this.scoutRepository = scoutRepository;
    }

    public void save(JamboreeForm form) {
        Period age = Period.between(form.getBirthDate(), LocalDate.of(2027, Month.JULY, 30));

        JamboreeInscription inscription = new JamboreeInscription()
            .setParticipantType(form.getParticipantType())
            .setSurname(form.getSurname())
            .setName(form.getName())
            .setFeltName(form.getFeltName())
            .setDni(form.getDni())
            .setPassportNumber(form.getPassportNumber())
            .setNationality(form.getNationality())
            .setBirthDate(form.getBirthDate())
            .setAgeAtJamboree("%d años, %d meses, %d días".formatted(age.getYears(), age.getMonths(), age.getDays()))
            .setGender(form.getGender())
            .setPhoneNumber(form.getPhoneNumber())
            .setEmail(form.getEmail())
            .setResident(form.isResident())
            .setMunicipality(form.getMunicipality())
            .setAddress(form.getAddress())
            .setCp(form.getCp())
            .setLocality(form.getLocality())
            .setBloodType(form.getBloodType())
            .setMedicalData(form.getMedicalData())
            .setMedication(form.getMedication())
            .setAllergies(form.getAllergies())
            .setVaccineProgram(form.isVaccineProgram())
            .setFoodIntolerances(form.getFoodIntolerances())
            .setSize(form.getSize())
            .setObservations(form.getObservations())
            .setDietPreference(form.getDietPreference());

        JamboreeContact mainContact = new JamboreeContact()
            .setSurname(form.getMainContact().getSurname())
            .setName(form.getMainContact().getName())
            .setMobilePhone(form.getMainContact().getMobilePhone())
            .setLandlinePhone(form.getMainContact().getLandlinePhone())
            .setEmail(form.getMainContact().getEmail())
            .setInscription(inscription);


        JamboreeContact secondaryContact = new JamboreeContact()
            .setSurname(form.getSecondaryContact().getSurname())
            .setName(form.getSecondaryContact().getName())
            .setMobilePhone(form.getSecondaryContact().getMobilePhone())
            .setEmail(form.getSecondaryContact().getEmail())
            .setInscription(inscription);


        inscription.setContacts(List.of(mainContact, secondaryContact));

        inscription.setLanguages(form.getLanguages().stream()
            .map(language -> new JamboreeLanguage()
                .setLanguage(language.getLanguage())
                .setLevel(language.getLevel())
                .setInscription(inscription)
            ).toList()
        );

        String census = scoutRepository.findFirstByPersonalDataIdDocumentNumber(inscription.getDni()).flatMap(scout -> Optional.ofNullable(scout.getCensus()).map(String::valueOf)).orElse(null);
        inscription.setCensus(census);

        jamboreeInscriptionRepository.save(inscription);
    }

    public ResponseEntity<byte[]> getExcel() {
        List<JamboreeInscription> list = jamboreeInscriptionRepository.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Asistentes Jamboree");

            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < JamboreeExcelConstants.HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(JamboreeExcelConstants.HEADERS[i]);
            }

            for (int i = 0; i < list.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                JamboreeInscription inscription = list.get(i);
                ExcelRowHelper excelRowHelper = new ExcelRowHelper(row);

                excelRowHelper.addRow(Optional.ofNullable(inscription.getCensus()).map("35-105-%s"::formatted).orElse(""));
                excelRowHelper.addRow(inscription.getParticipantType());
                excelRowHelper.addRow(inscription.getSurname().toUpperCase());
                excelRowHelper.addRow(inscription.getName().toUpperCase());
                excelRowHelper.addRow(Optional.ofNullable(inscription.getFeltName()).orElse("").toUpperCase());
                excelRowHelper.addRow(inscription.getDni().toUpperCase());
                excelRowHelper.addRow(inscription.getPassportNumber().toUpperCase());
                excelRowHelper.addRow(inscription.getNationality());
                excelRowHelper.addRow(inscription.isResident() ? "Sí" : "No");
                excelRowHelper.addRow(inscription.getMunicipality());
                excelRowHelper.addRow(inscription.getAddress());
                excelRowHelper.addRow(inscription.getCp());
                excelRowHelper.addRow(inscription.getLocality());
                excelRowHelper.addRow(inscription.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                Period age = Period.between(inscription.getBirthDate(), LocalDate.now());
                excelRowHelper.addRow("%d años, %d meses, %d días".formatted(age.getYears(), age.getMonths(), age.getDays()));
                excelRowHelper.addRow(inscription.getAgeAtJamboree());
                excelRowHelper.addRow(inscription.getGender());
                excelRowHelper.addRow(inscription.getPhoneNumber());
                excelRowHelper.addRow(inscription.getEmail());
                excelRowHelper.addRow(inscription.getBloodType());
                excelRowHelper.addRow(inscription.getMedicalData());
                excelRowHelper.addRow(inscription.getMedication());
                excelRowHelper.addRow(inscription.getAllergies());
                excelRowHelper.addRow(inscription.isVaccineProgram() ? "Sí" : "No");
                String languages = inscription.getLanguages().stream()
                    .map(language -> "%s (%s)".formatted(language.getLanguage(), language.getLevel()))
                    .collect(Collectors.joining(", "));
                excelRowHelper.addRow(languages);
                excelRowHelper.addRow(inscription.getSize());
                excelRowHelper.addRow(inscription.getFoodIntolerances());
                excelRowHelper.addRow(inscription.getDietPreference());

                Optional<JamboreeContact> mainContact = inscription.getContacts().stream().filter(contact -> contact.getLandlinePhone() != null).findFirst();
                if (mainContact.isPresent()) {
                    JamboreeContact contact = mainContact.get();
                    excelRowHelper.addRow(contact.getSurname().toUpperCase());
                    excelRowHelper.addRow(contact.getName().toUpperCase());
                    excelRowHelper.addRow(contact.getMobilePhone());
                    excelRowHelper.addRow(contact.getLandlinePhone());
                    excelRowHelper.addRow(contact.getEmail());
                } else {
                    IntStream.range(0, 5).forEach(ignore -> excelRowHelper.addRow("-"));
                }

                Optional<JamboreeContact> secondaryContact = inscription.getContacts().stream().filter(contact -> contact.getLandlinePhone() == null).findFirst();
                if (secondaryContact.isPresent()) {
                    JamboreeContact contact = secondaryContact.get();
                    excelRowHelper.addRow(Optional.ofNullable(contact.getSurname()).orElse("-").toUpperCase());
                    excelRowHelper.addRow(Optional.ofNullable(contact.getName()).orElse("-").toUpperCase());
                    excelRowHelper.addRow(Optional.ofNullable(contact.getMobilePhone()).orElse("-"));
                    excelRowHelper.addRow(Optional.ofNullable(contact.getEmail()).orElse("-"));
                } else {
                    IntStream.range(0, 4).forEach(ignore -> excelRowHelper.addRow("-"));
                }
                excelRowHelper.addRow(inscription.getObservations());
            }

            for (int i = 0; i < JamboreeExcelConstants.HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            workbook.write(out);
            return new FileTransferDto(
                out.toByteArray(),
                "asistentes-jamboree-a-%s".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy-HH_mm_ss"))),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ).asResponseEntity();

        } catch (IOException e) {
            log.error("Error while creating excel file: {}", e.getMessage());
            throw new WebBentayaErrorException("Error al crear el excel");
        }
    }

    private static class ExcelRowHelper {
        private final XSSFRow row;
        private int currentCell = 0;

        private ExcelRowHelper(XSSFRow row) {
            this.row = row;
        }

        private void addRow(String data) {
            this.row.createCell(currentCell++).setCellValue(data);
        }
    }
}
