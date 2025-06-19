package org.scouts105bentaya.features.donation;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.features.donation.dto.DonationDeclarationDto;
import org.scouts105bentaya.features.donation.dto.DonationDeductionDto;
import org.scouts105bentaya.features.donation.dto.DonationFileFormDto;
import org.scouts105bentaya.features.donation.dto.DonationLastTwoYearsDto;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DonationFileService {
    private static final String ASSOCIATION_NAME = "ASOCIACION SCOUTS EXPLORADORES BENTAYA";
    private static final String ASSOCIATION_CIF = "G35295641";

    private static final String DECLARANT_REGISTER_NUMBER = "1";
    private static final String DECLARATION_REGISTER_NUMBER = "2";
    private static final String MODEL_NUMBER = "182";
    private static final String SUPPORT_TYPE = "T";
    private static final String JUSTIFICATION_NUMBER = "1820000000000";
    private static final String DECLARANT_TYPE = "1";
    private static final String DECLARATION_KEY = "A";
    private static final String CANARY_ISLANDS_DEDUCTION = "05";
    private static final String NO_AUTONOMIC_DEDUCTION = "00";
    private static final String JURIDICAL_PERSON = "J";
    private static final String REAL_PERSON = "F";
    private static final String PROVINCE_CODE = "35";
    private static final Character IN_KIND = 'X';
    private static final String EIGHTY_PERCENT_DEDUCTION = "08000";
    private static final String FORTY_FIVE_PERCENT_DEDUCTION = "04500";
    private static final String FORTY_PERCENT_DEDUCTION = "04000";
    private static final Character RECURRENT = '1';
    private static final Character NOT_RECURRENT = '2';

    private static final Character ZERO_PAD = '0';
    private static final Character SPACE_PAD = ' ';
    private final DonationRepository donationRepository;

    public DonationFileService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public FileTransferDto generateDonationFile(DonationFileFormDto form) {
        List<DonationDeclarationDto> declarations = donationRepository.getDonationDeclarations(form.fiscalYear());

        this.validateForm(form);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Writer out = new OutputStreamWriter(baos, StandardCharsets.ISO_8859_1)) {
            writeDeclarantRegister(out, form, declarations);
            for (DonationDeclarationDto declaration : declarations) {
                writeDeclarationRegister(out, form, declaration);
            }
        } catch (IOException e) {
            log.error("Error generating donation file", e);
            throw new WebBentayaErrorException("Ha ocurrido un error generando el fichero");
        }
        return new FileTransferDto(baos.toByteArray(),
            "%s.%s".formatted(ASSOCIATION_CIF, MODEL_NUMBER),
            "text/plain");
    }

    private void writeDeclarationRegister(Writer out, DonationFileFormDto form, DonationDeclarationDto declaration) throws IOException {
        this.validateDeclaration(declaration);

        out.write(DECLARATION_REGISTER_NUMBER);
        out.write(MODEL_NUMBER);
        out.write(String.valueOf(form.fiscalYear()));
        out.write(stringWithZeroBefore(ASSOCIATION_CIF, 9));
        out.write(stringWithZeroBefore(declaration.getIdNumber(), 9));
        out.write(StringUtils.repeat(SPACE_PAD, 9));

        out.write(stringWithSpaceAfter(generateSurnameAndName(declaration.getName(), declaration.getSurname()), 40));
        out.write(PROVINCE_CODE);
        out.write(DECLARATION_KEY);

        DonationDeductionDto deduction = getDeductionPercent(declaration, form.fiscalYear());
        out.write(deduction.deductionPercent());
        out.write(intWithZeroBefore(declaration.getAmount(), 13));
        out.write(declaration.isInKind() ? IN_KIND : SPACE_PAD);

        boolean isJuridical = declaration.isJuridical();
        out.write(isJuridical ? NO_AUTONOMIC_DEDUCTION : CANARY_ISLANDS_DEDUCTION);
        int extraDeduction = isJuridical ? 0 : form.autonomousCommunityDeduction();
        out.write(intWithZeroBefore(extraDeduction, 5));
        out.write(isJuridical ? JURIDICAL_PERSON : REAL_PERSON);

        out.write(SPACE_PAD);
        out.write(StringUtils.repeat(ZERO_PAD, 4));
        out.write(StringUtils.repeat(SPACE_PAD, 21));

        out.write(deduction.recurrency());

        out.write(StringUtils.repeat(SPACE_PAD, 118));

        out.write(StringUtils.CR + StringUtils.LF);
        out.flush();
    }

    private DonationDeductionDto getDeductionPercent(DonationDeclarationDto declaration, int fiscalYear) {
        DonationLastTwoYearsDto lastTwoYears = donationRepository.getLastTwoYears(declaration.getIdNumber(), fiscalYear);

        boolean isRecurrent =
            lastTwoYears.getLastYear() > 0 &&
            lastTwoYears.getTwoYearsBefore() > 0 &&
            declaration.getAmount() >= lastTwoYears.getLastYear() &&
            lastTwoYears.getLastYear() >= lastTwoYears.getTwoYearsBefore();

        String deduction;
        if (declaration.getAmount() <= 25000) {
            deduction = EIGHTY_PERCENT_DEDUCTION;
        } else {
            deduction = isRecurrent ? FORTY_FIVE_PERCENT_DEDUCTION : FORTY_PERCENT_DEDUCTION;
        }
        return new DonationDeductionDto(deduction, isRecurrent ? RECURRENT : NOT_RECURRENT);
    }

    private void writeDeclarantRegister(Writer out, DonationFileFormDto form, List<DonationDeclarationDto> declarations) throws IOException {
        out.write(DECLARANT_REGISTER_NUMBER);
        out.write(MODEL_NUMBER);
        out.write(String.valueOf(form.fiscalYear()));
        out.write(stringWithZeroBefore(ASSOCIATION_CIF, 9));
        out.write(stringWithSpaceAfter(ASSOCIATION_NAME, 40));
        out.write(SUPPORT_TYPE);
        out.write(intWithZeroBefore(form.declarantRepresentativePhone(), 9));
        out.write(stringWithSpaceAfter(generateSurnameAndName(form.declarantRepresentativeName(), form.declarantRepresentativeSurname()), 40));
        out.write(JUSTIFICATION_NUMBER);
        out.write(StringUtils.repeat(SPACE_PAD, 2));
        out.write(StringUtils.repeat(ZERO_PAD, 13));
        out.write(intWithZeroBefore(declarations.size(), 9));
        out.write(intWithZeroBefore(sumAllDeclarations(declarations), 15));
        out.write(DECLARANT_TYPE);
        out.write(StringUtils.repeat(SPACE_PAD, 90));

        out.write(StringUtils.CR + StringUtils.LF);
        out.flush();
    }

    private static int sumAllDeclarations(List<DonationDeclarationDto> declarations) {
        return declarations.stream().reduce(0, (acc, declaration) -> acc + declaration.getAmount(), Integer::sum);
    }

    private static String stringWithSpaceAfter(String string, int size) {
        return stripNonLetterAccents(StringUtils.truncate(StringUtils.rightPad(string.trim(), size, SPACE_PAD), size));
    }

    private static String intWithZeroBefore(Integer value, int size) {
        return stringWithZeroBefore(String.valueOf(value), size);
    }

    private static String stringWithZeroBefore(String string, int size) {
        return stripNonLetterAccents(StringUtils.truncate(StringUtils.leftPad(string.trim(), size, ZERO_PAD), size));
    }

    private static String stripNonLetterAccents(String string) {
        String temp = string
            .toUpperCase()
            .replace("Ñ", "__ENYE__")
            .replace("Ç", "__CEDILLA__");
        return StringUtils.stripAccents(temp)
            .replace("__ENYE__", "Ñ")
            .replace("__CEDILLA__", "Ç")
            .replaceAll("[^A-Z0-9ÑÇ]", " ");
    }

    private static String generateSurnameAndName(String name, @Nullable String surname) {
        return String.format("%s %s", Optional.ofNullable(surname).orElse("").toUpperCase().trim(), name.toUpperCase().trim()).trim();
    }

    private void validateDeclaration(DonationDeclarationDto declaration) {
        if (declaration.getIdNumber() == null) {
            throw new WebBentayaBadRequestException("El declarante %s %s no tiene DOI especificado".formatted(declaration.getName(), declaration.getSurname()));
        }
        if (declaration.getName() == null) {
            throw new WebBentayaBadRequestException("El declarante con DOI %s no tiene nombre o razón social".formatted(declaration.getIdNumber()));
        }
    }

    private void validateForm(DonationFileFormDto dto) {
        if (dto.declarantRepresentativeName().length() + dto.declarantRepresentativeSurname().length() > 39) {
            throw new WebBentayaBadRequestException("Los nombre y apellidos no pueden superar los 39 caracteres");
        }
    }
}
