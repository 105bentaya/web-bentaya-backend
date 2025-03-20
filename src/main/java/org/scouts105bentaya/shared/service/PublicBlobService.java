package org.scouts105bentaya.shared.service;

import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublicBlobService extends GeneralBlobService {
    protected PublicBlobService(@Qualifier("publicContainer") BlobContainerClient blobContainerClient) {
        super(blobContainerClient, log);
    }
}
