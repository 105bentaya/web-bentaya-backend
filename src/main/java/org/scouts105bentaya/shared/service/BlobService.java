package org.scouts105bentaya.shared.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class BlobService {

    private static final int MAX_RETRIES = 4;

    private final BlobContainerClient blobContainerClient;

    public BlobService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public String uploadBlob(MultipartFile file) {
        String uuid = this.getNewUUid();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            blobContainerClient.getBlobClient(uuid).upload(inputStream);
            log.info("Blob {} successfully created", uuid);
            return uuid;
        } catch (IOException e) {
            throw new WebBentayaErrorException(e.getMessage());
        }
    }

    private String getNewUUid() {
        int currentTry = 0;
        while (currentTry <= MAX_RETRIES) {
            String uuid = UUID.randomUUID().toString();
            if (Boolean.FALSE.equals(blobContainerClient.getBlobClient(uuid).exists())) {
                return uuid;
            }
            log.info("Blob {} already exists, retry number {}", uuid, ++currentTry);
        }
        log.error("Could not create unique blob after {} attempts", MAX_RETRIES + 1);
        throw new WebBentayaErrorException("No se ha podido guardar el archivo");
    }

    public byte[] getBlob(String uuid) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobContainerClient.getBlobClient(uuid).downloadStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException | BlobStorageException e) {
            log.error("Error while reading blob {}", uuid, e);
            throw new WebBentayaErrorException("No se ha podido obtener el archivo");
        }
    }

    public void deleteBlob(String uuid) {
        blobContainerClient.getBlobClient(uuid).delete();
        log.info("Blob {} successfully deleted", uuid);
    }
}
