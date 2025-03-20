package org.scouts105bentaya.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private FileUtils() {
    }

    public static ResponseEntity<byte[]> getFileResponseEntity(byte[] file, String fileName, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Filename", fileName);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Filename");
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(file);
    }

    public static ResponseEntity<byte[]> getFileResponseEntity(byte[] file, String fileName, String mediaType) {
        return getFileResponseEntity(file, fileName, MediaType.parseMediaType(mediaType));
    }

    public static void validateFileIsDoc(MultipartFile file) {
        if (!DOC_TYPES.containsValue(file.getContentType())) {
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
        if (!fileType.startsWith("image/")) {
            throw new WebBentayaBadRequestException("El archivo debe ser una imagen");
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
