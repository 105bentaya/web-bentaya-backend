package org.scouts105bentaya.core.migration;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class JavaDocumentMigration implements InitializingBean {

    private final BlobContainerClient blobContainerClient;
    private final BookingDocumentRepository bookingDocumentRepository;

    public JavaDocumentMigration(BlobContainerClient blobContainerClient, BookingDocumentRepository bookingDocumentRepository) {
        this.blobContainerClient = blobContainerClient;
        this.bookingDocumentRepository = bookingDocumentRepository;
    }

    @Override
    public void afterPropertiesSet() {
        bookingDocumentRepository.findAll().stream()
            .filter(document -> document.getFileName() == null && document.getFileData() != null)
            .forEach(document -> {
            String fileUuid = UUID.randomUUID().toString();
            BlobClient blob = blobContainerClient.getBlobClient(fileUuid);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(document.getFileData())) {
                blob.upload(inputStream);
            } catch (IOException e) {
                throw new WebBentayaErrorException(e.getMessage());
            }

            document.setFileUuid(fileUuid);
            bookingDocumentRepository.save(document);
        });
    }
}
