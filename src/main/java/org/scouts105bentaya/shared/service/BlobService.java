package org.scouts105bentaya.shared.service;

import com.azure.storage.blob.BlobContainerClient;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class BlobService {

    private final BlobContainerClient blobContainerClient;

    public BlobService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public String uploadBlob(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            blobContainerClient.getBlobClient(uuid).upload(inputStream);
            return uuid;
        } catch (IOException e) {
            throw new WebBentayaErrorException(e.getMessage());
        }
    }

    public byte[] getBlob(String uuid) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobContainerClient.getBlobClient(uuid).downloadStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new WebBentayaErrorException(e.getMessage());
        }
    }

    public void deleteBlob(String uuid) {
        blobContainerClient.getBlobClient(uuid).delete();
    }
}
