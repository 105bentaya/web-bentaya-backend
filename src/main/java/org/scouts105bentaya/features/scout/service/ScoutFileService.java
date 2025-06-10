package org.scouts105bentaya.features.scout.service;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.enums.ScoutFileType;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRecordRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class ScoutFileService {

    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final ScoutRepository scoutRepository;
    private final ScoutRecordRepository scoutRecordRepository;

    public ScoutFileService(
        BlobService blobService,
        ScoutFileRepository scoutFileRepository,
        ScoutRepository scoutRepository, ScoutRecordRepository scoutRecordRepository) {
        this.blobService = blobService;
        this.scoutFileRepository = scoutFileRepository;
        this.scoutRepository = scoutRepository;
        this.scoutRecordRepository = scoutRecordRepository;
    }

    public ResponseEntity<byte[]> downloadScoutFile(Integer id) {
        ScoutFile file = scoutFileRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        return new FileTransferDto(blobService.getBlob(file.getUuid()), file.getName(), file.getMimeType()).asResponseEntity();
    }

    @Synchronized
    public ScoutFile createScoutFile(Integer entityId, MultipartFile file, ScoutFileType fileType, String customName) {
        FileUtils.validateFileIsPdf(file);
        ScoutFile scoutFile = buildScoutFile(file, customName);

        if (fileType == ScoutFileType.RECORD) {
            attachFileToScoutRecord(scoutFile, entityId);
        } else {
            attachFileToScout(scoutFile, fileType, entityId);
        }

        return scoutFile;
    }

    private ScoutFile buildScoutFile(MultipartFile file, String customName) {
        ScoutFile scoutFile = new ScoutFile();
        scoutFile.setName(file.getOriginalFilename());
        scoutFile.setMimeType(file.getContentType());
        scoutFile.setUuid(blobService.createBlob(file));
        scoutFile.setUploadDate(ZonedDateTime.now());
        scoutFile.setCustomName(StringUtils.isBlank(customName) ? null : customName);
        return scoutFileRepository.save(scoutFile);
    }

    private void attachFileToScoutRecord(ScoutFile scoutFile, Integer id) {
        ScoutRecord scoutRecord = scoutRecordRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.getFiles().add(scoutFile);
        scoutRecordRepository.save(scoutRecord);
    }

    private void attachFileToScout(ScoutFile scoutFile, ScoutFileType type, Integer id) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        getFileTypeList(scout, type).add(scoutFile);
        scoutRepository.save(scout);
    }

    private List<ScoutFile> getFileTypeList(Scout scout, ScoutFileType type) {
        return switch (type) {
            case MEDICAL -> scout.getMedicalData().getDocuments();
            case PERSONAL -> scout.getPersonalData().getDocuments();
            case ECONOMIC -> scout.getEconomicData().getDocuments();
            default -> throw new WebBentayaBadRequestException("Unsupported file type: " + type);
        };
    }

    public void deleteScoutFile(Integer entityId, Integer fileId, ScoutFileType type) {
        if (type == ScoutFileType.RECORD) {
            deleteRecordFile(entityId, fileId);
        } else {
            deleteScoutFileFromScout(entityId, fileId, type);
        }
        scoutFileRepository.deleteById(fileId);
    }

    private void deleteRecordFile(Integer recordId, Integer fileId) {
        ScoutRecord scoutRecord = scoutRecordRepository.findById(recordId).orElseThrow(WebBentayaNotFoundException::new);
        this.deleteFileAndBlob(scoutRecord.getFiles(), fileId);
        scoutRecordRepository.save(scoutRecord);
    }

    private void deleteScoutFileFromScout(Integer scoutId, Integer fileId, ScoutFileType type) {
        Scout scout = scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new);
        this.deleteFileAndBlob(getFileTypeList(scout, type), fileId);
        scoutRepository.save(scout);
    }

    private void deleteFileAndBlob(List<ScoutFile> files, Integer fileId) {
        ScoutFile scoutFile = files.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);
        blobService.deleteBlob(scoutFile.getUuid());
        files.remove(scoutFile);
    }
}
