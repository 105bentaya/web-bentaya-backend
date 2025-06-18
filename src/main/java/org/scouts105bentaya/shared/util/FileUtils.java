package org.scouts105bentaya.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class FileUtils {

    static final Map<String, String> DOC_TYPES = Map.ofEntries(
        Map.entry(".doc", "application/msword"),
        Map.entry(".dot", "application/msword"),
        Map.entry(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        Map.entry(".dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template"),
        Map.entry(".odt", "application/vnd.oasis.opendocument.text"),
        Map.entry(".rtf", "application/rtf")
    );

    static final Map<String, String> IMG_TYPES = Map.ofEntries(
        Map.entry(".webp", "image/webp"),
        Map.entry(".jpg", "image/jpeg"),
        Map.entry(".png", "image/png"),
        Map.entry(".svg", "image/svg+xml")
    );

    static final Map<String, String> PDF_TYPES = Map.ofEntries(
        Map.entry(".pdf", "application/pdf")
    );

    static final Map<String, String> EXCEL_TYPES = Map.ofEntries(
        Map.entry(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    );

    private FileUtils() {
    }

    public static void validateFileType(MultipartFile file, FileTypeEnum... allowedTypes) {
        if (Arrays.stream(allowedTypes).noneMatch(allowedType -> allowedType.fileIsValid(file))) {
            log.warn("Unsupported document type: {}", file.getContentType());
            throw new WebBentayaBadRequestException("El formato del archivo no admitido. Tipos permitidos: %s".formatted(getSupportedTypes(allowedTypes)));
        }
    }

    public static void validateFilesType(List<MultipartFile> files, FileTypeEnum... allowedTypes) {
        files.forEach(file -> validateFileType(file, allowedTypes));
    }

    private static String getSupportedTypes(FileTypeEnum... allowedTypes) {
        return Arrays.stream(allowedTypes).flatMap(type -> type.validExtensions().stream()).collect(Collectors.joining(", "));
    }
}
