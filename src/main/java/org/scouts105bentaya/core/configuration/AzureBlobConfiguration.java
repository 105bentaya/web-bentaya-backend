package org.scouts105bentaya.core.configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AzureBlobConfiguration {

    @Value("${azure.blob.connection}")
    private String connectionString;

    @Bean
    public BlobServiceClient serviceConnection() {
        log.info("METHOD AzureBlobConfiguration.serviceConnection {}",connectionString);
        return new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
    }

    @Bean
    public BlobContainerClient containerConnection(BlobServiceClient serviceClient) {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient("web-bentaya-files");
        if (containerClient.createIfNotExists()) {
            log.info("Blob container {} created", containerClient.getBlobContainerName());
        }
        return containerClient;
    }
}
