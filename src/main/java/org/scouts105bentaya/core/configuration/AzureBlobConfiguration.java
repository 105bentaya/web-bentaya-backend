package org.scouts105bentaya.core.configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class AzureBlobConfiguration {

    @Value("${azure.blob.connection}")
    private String connectionString;

    @Bean
    public BlobServiceClient serviceConnection() {
        return new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
    }

    @Bean
    @Primary
    public BlobContainerClient privateContainer(BlobServiceClient serviceClient) {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient("web-bentaya-files");
        if (containerClient.createIfNotExists()) {
            log.info("Blob private container {} created", containerClient.getBlobContainerName());
        }
        return containerClient;
    }

    @Bean
    public BlobContainerClient publicContainer(BlobServiceClient serviceClient) {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient("web-bentaya-public-files");
        if (containerClient.createIfNotExists()) {
            log.info("Blob public container {} created", containerClient.getBlobContainerName());
        }
        return containerClient;
    }
}
