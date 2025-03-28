package org.scouts105bentaya.shared.util.dto;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileTransferDto {
    private byte[] file;
    private String fileName;
    private MediaType mediaType;

    public FileTransferDto(byte[] file, String fileName, MediaType mediaType) {
        this.file = file;
        this.fileName = fileName;
        this.mediaType = mediaType;
    }

    public FileTransferDto(byte[] file, String fileName, String mediaType) {
        this.file = file;
        this.fileName = fileName;
        this.mediaType = MediaType.parseMediaType(mediaType);
    }

    public ResponseEntity<byte[]> asResponseEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Filename", fileName);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Filename");
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(file);
    }

    public DataSource asDataSource() {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(file, mediaType.toString());
        dataSource.setName(fileName);
        return dataSource;
    }
}
