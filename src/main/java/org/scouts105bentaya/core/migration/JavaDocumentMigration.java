package org.scouts105bentaya.core.migration;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
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
        log.info("Initializing JavaDocumentMigration");
        bookingDocumentRepository.findAll().stream()
            .filter(document -> document.getFileUuid() == null && document.getFileData() != null)
            .forEach(document -> {
                log.info("Migrating document {}", document.getId());
                String fileUuid = UUID.randomUUID().toString();
                BlobClient blob = blobContainerClient.getBlobClient(fileUuid);

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(document.getFileData())) {
                    blob.upload(inputStream);
                } catch (IOException e) {
                    throw new WebBentayaErrorException(e.getMessage());
                }

                document.setFileUuid(fileUuid);
                bookingDocumentRepository.save(document);
                log.info("Document {} migrated as {}", document.getId(), document.getFileUuid());
            });
        log.info("JavaDocumentMigration completed");
    }
}
