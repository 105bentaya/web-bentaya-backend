package org.scouts105bentaya.features.scout.service;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.ScoutExcelDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutExcelField;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.BloodType;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;
import org.scouts105bentaya.shared.util.ExcelUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScoutExcelService {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ScoutService scoutService;

    public ScoutExcelService(ScoutService scoutService) {
        this.scoutService = scoutService;
    }

    public ByteArrayOutputStream downloadScoutExcel(ScoutExcelDto dto) {
        this.validateDto(dto);

        ScoutSpecificationFilter filter = dto.filter();
        filter.setUnpaged();
        List<Scout> scouts = scoutService.findAll(filter).toList();

        List<ScoutExcelField> fields = dto.fields();

        Map<String, Integer> maxSizes = calculateMaxSizes(fields, scouts);
        List<String> headers = buildHeaders(fields, maxSizes);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Censo 105 Bentaya");

            writeHeaderRow(sheet, headers);
            writeDataRows(sheet, scouts, fields, maxSizes);

            ExcelUtils.autosizeSheet(sheet, headers.size());
            ExcelUtils.createTable(sheet, scouts.size(), headers.size() - 1);

            workbook.write(out);
            return out;
        } catch (IOException e) {
            log.error("downloadScoutExcel - exception whilst generating excel: {}", e.getMessage(), e);
            throw new WebBentayaErrorException("Error al exportar el excel");
        }
    }

    private Map<String, Integer> calculateMaxSizes(List<ScoutExcelField> fields, List<Scout> scouts) {
        return fields.stream()
            .filter(f -> !CollectionUtils.isEmpty(f.listFields()))
            .collect(Collectors.toMap(
                ScoutExcelField::field,
                f -> scouts.stream()
                    .mapToInt(list -> ((List<?>) Objects.requireNonNull(getFieldValue(list, f.field()))).size())
                    .max()
                    .orElse(0)
            ));
    }

    private List<String> buildHeaders(List<ScoutExcelField> fields, Map<String, Integer> maxSizes) {
        List<String> headers = new ArrayList<>();
        for (ScoutExcelField field : fields) {
            if (CollectionUtils.isEmpty(field.listFields())) {
                headers.add(field.label());
            } else {
                int max = maxSizes.getOrDefault(field.field(), 0);
                for (int i = 1; i <= max; i++) {
                    for (ScoutExcelField sub : field.listFields()) {
                        headers.add(String.format("%s %d - %s", field.label(), i, sub.label()));
                    }
                }
            }
        }
        return headers;
    }

    private void writeHeaderRow(XSSFSheet sheet, List<String> headers) {
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }
    }

    private void writeDataRows(XSSFSheet sheet, List<Scout> scouts, List<ScoutExcelField> fields, Map<String, Integer> maxSizes) {
        for (int rowIndex = 0; rowIndex < scouts.size(); rowIndex++) {
            XSSFRow row = sheet.createRow(rowIndex + 1);
            AtomicInteger col = new AtomicInteger(0);
            for (ScoutExcelField field : fields) {
                insertRowData(row, scouts.get(rowIndex), field, maxSizes.getOrDefault(field.field(), 0), col);
            }
        }
    }

    private void insertRowData(XSSFRow row, Scout scout, ScoutExcelField columnDto, int maxListSize, AtomicInteger colIndex) {
        if (CollectionUtils.isEmpty(columnDto.listFields())) {
            String value = formatValue(extractScoutValue(scout, columnDto), columnDto.pipe());
            row.createCell(colIndex.getAndIncrement()).setCellValue(value);
        } else {
            List<?> list = Objects.requireNonNull((List<?>) getFieldValue(scout, columnDto.getFields()));
            for (Object item : list) {
                for (ScoutExcelField sub : columnDto.listFields()) {
                    String val = formatValue(getFieldValue(item, sub.getFieldsFromList()), columnDto.pipe());
                    row.createCell(colIndex.getAndIncrement()).setCellValue(val);
                }
            }
            int missingCells = maxListSize - list.size();
            for (int i = 0; i < missingCells; i++) {
                for (int j = 0; j < columnDto.listFields().size(); j++) {
                    row.createCell(colIndex.getAndIncrement());
                }
            }
        }
    }

    private Object extractScoutValue(Scout scout, ScoutExcelField field) {
        String[] fields = field.getFields();
        return fields[0].startsWith("custom") && fields.length == 2 ?
            getCustomFields(fields[1], scout) :
            getFieldValue(scout, fields);
    }

    private String formatValue(Object extractedValue, @Nullable String pipe) {
        if (extractedValue == null) {
            return "";
        }
        if (pipe == null) {
            return extractedValue.toString();
        }
        return switch (pipe) {
            case "BOOLEAN" -> (Boolean) extractedValue ? "Sí" : "No";
            case "LOCAL_DATE" -> LOCAL_DATE_FORMATTER.format((LocalDate) extractedValue);
            case "STATUS" -> ((ScoutStatus) extractedValue).getStringValue();
            case "BLOOD_TYPE" -> ((BloodType) extractedValue).getStringValue();
            case "CENSUS" -> "35-105-" + extractedValue;
            default -> extractedValue.toString();
        };
    }

    private String getCustomFields(String key, Scout scout) {
        return switch (key) {
            case "group" -> ScoutUtils.getScoutGroupName(scout);
            case "scoutGroup" -> ScoutUtils.getScoutGroupSection(scout);
            case "section" -> ScoutUtils.getScoutSection(scout);
            case "age" -> ExcelUtils.getAge(scout.getPersonalData().getBirthday());
            default -> "";
        };
    }

    private Object getFieldValue(Object obj, String... fields) {
        try {
            for (String part : fields) {
                if (obj == null) return null;
                Field field = obj.getClass().getDeclaredField(part);
                field.setAccessible(true);
                obj = field.get(obj);
            }
            return obj;
        } catch (Exception e) {
            log.warn("Failed to extract fields {}: {}", Arrays.toString(fields), e.getMessage());
            throw new WebBentayaErrorException(e.getMessage());
        }
    }

    private void validateDto(ScoutExcelDto scoutExcelDto) {
        scoutExcelDto.fields().forEach(field -> {
            List<ScoutExcelField> keys = field.listFields();
            if (CollectionUtils.isEmpty(keys)) {
                validateKey(field);
            } else {
                keys.forEach(this::validateKey);
            }
        });
    }

    private void validateKey(ScoutExcelField field) {
        if (!allowedFields.contains(field.field())) {
            log.warn("Field {} is not allowed", field.field());
            throw new WebBentayaConflictException("Campo no permitido");
        }
    }

    private static final List<String> allowedFields = List.of(
        "census",
        "custom.group",
        "custom.scoutGroup",
        "custom.section",
        "status",
        "federated",
        "personalData.name",
        "personalData.surname",
        "personalData.feltName",
        "personalData.gender",
        "personalData.birthday",
        "custom.age",
        "personalData.idDocument.number",
        "personalData.phone",
        "personalData.landline",
        "personalData.email",
        "personalData.shirtSize",
        "personalData.largeFamily",
        "personalData.imageAuthorization",
        "personalData.observations",
        "personalData.birthplace",
        "personalData.birthProvince",
        "personalData.nationality",
        "personalData.address",
        "personalData.city",
        "personalData.province",
        "personalData.residenceMunicipality",
        "contactList.name",
        "contactList.surname",
        "contactList.relationship",
        "contactList.donor",
        "contactList.idDocument.number",
        "contactList.phone",
        "contactList.email",
        "contactList.studies",
        "contactList.profession",
        "contactList.companyName",
        "contactList.observations",
        "medicalData.bloodType",
        "medicalData.socialSecurityNumber",
        "medicalData.privateInsuranceNumber",
        "medicalData.privateInsuranceEntity",
        "medicalData.foodIntolerances",
        "medicalData.foodAllergies",
        "medicalData.foodProblems",
        "medicalData.foodDiet",
        "medicalData.foodMedication",
        "medicalData.medicalIntolerances",
        "medicalData.medicalAllergies",
        "medicalData.medicalDiagnoses",
        "medicalData.medicalPrecautions",
        "medicalData.medicalMedications",
        "medicalData.medicalEmergencies",
        "medicalData.addictions",
        "medicalData.tendencies",
        "medicalData.records",
        "medicalData.bullyingProtocol",
        "economicData.iban",
        "economicData.bank",
        "scoutHistory.progressions",
        "scoutHistory.observations"
    );
}
