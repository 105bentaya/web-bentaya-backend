package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

@Slf4j
@Service
public class ScoutFileService {

    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;

    public ScoutFileService(
        BlobService blobService,
        ScoutFileRepository scoutFileRepository
    ) {
        this.blobService = blobService;
        this.scoutFileRepository = scoutFileRepository;
    }

    public ResponseEntity<byte[]> downloadScoutFile(Integer id) {
        ScoutFile file = scoutFileRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        return new FileTransferDto(blobService.getBlob(file.getUuid()), file.getName(), file.getMimeType()).asResponseEntity();
    }

    public ScoutFile createScoutFile(MultipartFile file) {
        ScoutFile scoutFile = new ScoutFile();
        scoutFile.setName(file.getOriginalFilename());
        scoutFile.setMimeType(file.getContentType());
        scoutFile.setUuid(blobService.createBlob(file));
        scoutFile.setUploadDate(ZonedDateTime.now());
        return scoutFile;
    }
}
