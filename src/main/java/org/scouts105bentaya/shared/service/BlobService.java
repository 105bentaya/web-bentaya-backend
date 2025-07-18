package org.scouts105bentaya.shared.service;

import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlobService extends GeneralBlobService {
    protected BlobService(BlobContainerClient blobContainerClient) {
        super(blobContainerClient, log);
    }
}