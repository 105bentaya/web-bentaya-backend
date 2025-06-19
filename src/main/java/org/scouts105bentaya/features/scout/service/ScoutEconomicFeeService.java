package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.entity.InvoiceIncomeType;
import org.scouts105bentaya.features.invoice.repository.InvoiceIncomeTypeRepository;
import org.scouts105bentaya.features.scout.dto.ScoutDonorDto;
import org.scouts105bentaya.features.scout.dto.form.DonationFeeFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.EconomicEntryDonor;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.EntryType;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.scout.repository.EconomicEntryRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;
import org.scouts105bentaya.shared.util.ExcelUtils;
import org.scouts105bentaya.shared.util.FileTypeEnum;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScoutEconomicFeeService {
    private static final int COL_CENSUS = 0;
    private static final int COL_AMOUNT = 1;
    private static final String HEADER_CENSUS = "censo";
    private static final String HEADER_AMOUNT = "importe";
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(?:[.,]\\d{1,2})?$");

    private final InvoiceIncomeTypeRepository invoiceIncomeTypeRepository;
    private final ScoutService scoutService;
    private final EconomicEntryRepository economicEntryRepository;
    private final ScoutRepository scoutRepository;
    private final ScoutEconomicDataService scoutEconomicDataService;

    public ScoutEconomicFeeService(
        InvoiceIncomeTypeRepository invoiceIncomeTypeRepository,
        ScoutService scoutService,
        EconomicEntryRepository economicEntryRepository,
        ScoutRepository scoutRepository,
        ScoutEconomicDataService scoutEconomicDataService
    ) {
        this.invoiceIncomeTypeRepository = invoiceIncomeTypeRepository;
        this.scoutService = scoutService;
        this.economicEntryRepository = economicEntryRepository;
        this.scoutRepository = scoutRepository;
        this.scoutEconomicDataService = scoutEconomicDataService;
    }

    @Transactional
    public void addFees(DonationFeeFormDto formDto, MultipartFile file) {
        InvoiceIncomeType donationType = invoiceIncomeTypeRepository.findById(formDto.donationTypeId()).orElseThrow(WebBentayaNotFoundException::new);
        if (!donationType.isDonation()) {
            throw new WebBentayaBadRequestException("No puede añadir cuotas de este tipo");
        }

        if (formDto.applyToCurrentScouts() && file != null) {
            throw new WebBentayaBadRequestException("No se puede aplicar a todas las altas y subir un excel");
        } else if (!formDto.applyToCurrentScouts() && file == null) {
            throw new WebBentayaBadRequestException("Debe subir un excel");
        }

        if (formDto.applyToCurrentScouts()) {
            addFeesToActiveScouts(formDto);
        } else {
            addFeesToExcelResult(formDto, this.parseAndValidateExcel(file));
        }
    }

    private void addFeesToActiveScouts(DonationFeeFormDto formDto) {
        ScoutSpecificationFilter filter = new ScoutSpecificationFilter();
        filter.setUnpaged();
        filter.setStatuses(List.of(ScoutStatus.ACTIVE));
        filter.setScoutTypes(List.of(ScoutType.SCOUT));
        List<Scout> scouts = scoutService.findAll(filter).toList();

        scouts.forEach(scout -> economicEntryRepository.save(this.fromForm(formDto, scout)));
    }

    private void addFeesToExcelResult(DonationFeeFormDto formDto, Map<Integer, Integer> scoutAmounts) {
        scoutAmounts.forEach((scoutCensus, amount) -> {
            Scout scout = scoutRepository.findByCensus(scoutCensus).orElseThrow(() -> new WebBentayaNotFoundException("No se ha encontrado un scout con censo %d".formatted(scoutCensus)));
            EconomicEntry entry = fromForm(formDto, scout);
            entry.setAmount(amount);
            economicEntryRepository.save(entry);
        });
    }

    private EconomicEntry fromForm(DonationFeeFormDto formDto, Scout scout) {
        EconomicEntry entry = new EconomicEntry();
        entry.setIssueDate(formDto.issueDate())
            .setDueDate(formDto.dueDate())
            .setAccount(formDto.account())
            .setType(EntryType.DONATION)
            .setAmount(formDto.amount())
            .setIncomeType(invoiceIncomeTypeRepository.findById(formDto.donationTypeId()).orElseThrow(WebBentayaNotFoundException::new))
            .setDescription(formDto.description())
            .setEconomicData(scout.getEconomicData());

        ScoutDonorDto donorDto = scoutEconomicDataService.getScoutDonor(scout);
        IdentificationDocument donorId = donorDto.idDocument();

        this.validateDonor(donorDto, scout);

        EconomicEntryDonor donor = new EconomicEntryDonor();
        donor.setName(donorDto.name())
            .setSurname(donorDto.surname())
            .setPersonType(donorDto.personType())
            .setIdDocument(new IdentificationDocument().setIdType(donorId.getIdType()).setNumber(donorId.getNumber()))
            .setEconomicEntry(entry);

        entry.setDonor(donor);

        return entry;
    }

    private void validateDonor(ScoutDonorDto donorDto, Scout scout) {
        if (donorDto.name() == null || donorDto.idDocument() == null) {
            String name = scout.getPersonalData().getName() + " " + scout.getPersonalData().getSurname();
            throw new WebBentayaBadRequestException(
                "No se pueden crear los apuntes por que la asociada %s (censo %s), no tiene el nombre y el DNI del donante especificados"
                    .formatted(name, scout.getCensus())
            );
        }
    }

    private Map<Integer, Integer> parseAndValidateExcel(MultipartFile file) {
        FileUtils.validateFileType(file, FileTypeEnum.EXCEL_TYPE);

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));

            Map<Integer, Integer> censusToAmount = new HashMap<>();
            List<Integer> invalidRows = parseRows(sheet, censusToAmount);

            if (!invalidRows.isEmpty()) {
                throw new WebBentayaBadRequestException(
                    "Hay errores en las siguientes filas: " + invalidRows.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
                );
            }

            return censusToAmount;
        } catch (IOException e) {
            log.error("parseAndValidateExcel - error whilst reading file", e);
            throw new WebBentayaErrorException("Error al procesar el archivo de Excel");
        }
    }

    private void validateHeader(XSSFRow header) {
        if (
            !ExcelUtils.rowCellHasValue(header, COL_CENSUS, HEADER_CENSUS) ||
            !ExcelUtils.rowCellHasValue(header, COL_AMOUNT, HEADER_AMOUNT)
        ) {
            throw new WebBentayaBadRequestException(
                "Cabeceras inválidas. Los valores deberían ser '%s' y '%s'".formatted(HEADER_CENSUS, HEADER_AMOUNT)
            );
        }
    }

    private List<Integer> parseRows(XSSFSheet sheet, Map<Integer, Integer> censusToAmount) {
        List<Integer> invalidRows = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) continue;

            try {
                int census = parseCensusCell(row.getCell(COL_CENSUS));
                int amount = parseAmountCell(row.getCell(COL_AMOUNT));

                if (censusToAmount.containsKey(census)) {
                    throw new WebBentayaBadRequestException(
                        "El censo de la fila %d ya sale en una fila anterior".formatted(i + 1)
                    );
                }
                censusToAmount.put(census, amount);

            } catch (ValidationException e) {
                log.warn("validateExcel - fila {} - {}", i + 1, e.getMessage());
                invalidRows.add(i + 1);
            }
        }

        return invalidRows;
    }

    private int parseCensusCell(XSSFCell cell) throws ValidationException {
        if (cell == null) {
            throw new ValidationException("Celda de censo vacía");
        }
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
                default -> Integer.parseInt(cell.getRawValue().trim());
            };
        } catch (Exception e) {
            throw new ValidationException("Formato de censo inválido");
        }
    }

    private int parseAmountCell(XSSFCell cell) throws ValidationException {
        if (cell == null) {
            throw new ValidationException("Celda de importe vacía");
        }
        String raw = switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue().trim();
            default -> cell.getRawValue().trim();
        };
        if (!AMOUNT_PATTERN.matcher(raw).matches()) {
            throw new ValidationException("Formato de importe inválido");
        }
        try {
            float value = Float.parseFloat(raw.replace(",", "."));
            return Math.round(value * 100);
        } catch (NumberFormatException e) {
            throw new ValidationException("Formato de importe inválido");
        }
    }

    private static class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }
}
