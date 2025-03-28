package org.scouts105bentaya.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class FileUtils {
    private static final Map<String, String> DOC_TYPES = Map.ofEntries(
        Map.entry(".doc", "application/msword"),
        Map.entry(".dot", "application/msword"),
        Map.entry(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        Map.entry(".dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template"),
        Map.entry(".odt", "application/vnd.oasis.opendocument.text"),
        Map.entry(".rtf", "application/rtf")
    );

    private static final Map<String, String> IMG_TYPES = Map.ofEntries(
        Map.entry(".webp", "image/webp"),
        Map.entry(".jpg", "image/jpeg"),
        Map.entry(".png", "image/png"),
        Map.entry(".svg", "image/svg+xml")
    );

    private FileUtils() {
    }

    public static void validateFileIsDoc(MultipartFile file) {
        String fileType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!DOC_TYPES.containsValue(fileType)) {
            logUnsupportedType(file);
            throw new WebBentayaBadRequestException("El archivo debe ser de tipo .docx, .odt o .rtf");
        }
    }

    public static void validateFileIsPdf(MultipartFile file) {
        if (!Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF.toString())) {
            logUnsupportedType(file);
            throw new WebBentayaBadRequestException("El archivo debe ser de tipo .pdf");
        }
    }

    public static void validateFileIsImg(MultipartFile file) {
        String fileType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!IMG_TYPES.containsValue(fileType)) {
            throw new WebBentayaBadRequestException("El archivo debe ser una imagen jpg, png o svg");
        }
    }

    public static void validateFilesIsImg(List<MultipartFile> files) {
        files.forEach(FileUtils::validateFileIsImg);
    }

    public static void validateFileIsDocOrPdf(MultipartFile file) {
        if (!(Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF.toString()) || DOC_TYPES.containsValue(file.getContentType()))) {
            logUnsupportedType(file);
            throw new WebBentayaBadRequestException("El archivo debe ser de tipo .pdf, .docx, .odt o .rtf");
        }
    }

    private static void logUnsupportedType(MultipartFile file) {
        log.warn("Unsupported document type: {}", file.getContentType());
    }
}
