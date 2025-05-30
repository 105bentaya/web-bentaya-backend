package org.scouts105bentaya.features.scout.service;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class ScoutEconomicDataService {

    private final ScoutRepository scoutRepository;
    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final ScoutFileService scoutFileService;

    public ScoutEconomicDataService(
        ScoutRepository scoutRepository,
        BlobService blobService,
        ScoutFileRepository scoutFileRepository,
        ScoutFileService scoutFileService
    ) {
        this.scoutRepository = scoutRepository;
        this.blobService = blobService;
        this.scoutFileRepository = scoutFileRepository;
        this.scoutFileService = scoutFileService;
    }

    public Scout updateEconomicData(Integer id, EconomicDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        EconomicData data = scout.getEconomicData();

        scout.getContactList().forEach(contact -> contact.setDonor(false));
        if (form.donorId() != null) {
            scout.getContactList().stream()
                .filter(contact -> contact.getId().equals(form.donorId()))
                .findFirst().orElseThrow(WebBentayaNotFoundException::new)
                .setDonor(true);
        }

        data.setIban(form.iban());
        data.setBank(form.bank());

        return scoutRepository.save(scout);
    }

    @Synchronized
    public ScoutFile uploadEconomicDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = scoutFileService.createScoutFile(file);
        scout.getEconomicData().getDocuments().add(scoutFile);

        scoutRepository.save(scout);
        return scoutFile;
    }

    public void deleteEconomicDataFile(Integer scoutId, Integer fileId) {
        Scout scout = scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> scoutFiles = scout.getEconomicData().getDocuments();

        ScoutFile scoutFile = scoutFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        scoutFiles.remove(scoutFile);
        scoutRepository.save(scout);

        scoutFileRepository.deleteById(fileId);
    }
}
