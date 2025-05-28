package org.scouts105bentaya.features.scout.service;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class ScoutPersonalDataService {

    private final ScoutRepository scoutRepository;
    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final ScoutFileService scoutFileService;

    public ScoutPersonalDataService(
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

    public Scout updatePersonalData(Integer id, PersonalDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        PersonalData data = scout.getPersonalData();
        data.setSurname(form.surname());
        data.setName(form.name());
        data.setFeltName(form.feltName());
        data.setBirthday(form.birthday());
        data.setBirthplace(form.birthplace());
        data.setBirthProvince(form.birthProvince());
        data.setNationality(form.nationality());
        data.setAddress(form.address());
        data.setCity(form.city());
        data.setProvince(form.province());
        data.setPhone(form.phone());
        data.setLandline(form.landline());
        data.setEmail(form.email());
        data.setShirtSize(form.shirtSize());
        data.setResidenceMunicipality(form.residenceMunicipality());
        data.setGender(form.gender());

        data.setIdDocument(ScoutUtils.updateIdDocument(data.getIdDocument(), form.idDocument()));
        data.setObservations(form.observations());

        return scoutRepository.save(scout);
    }

    @Synchronized
    public ScoutFile uploadPersonalDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = scoutFileService.createScoutFile(file);
        scout.getPersonalData().getDocuments().add(scoutFile);

        scoutRepository.save(scout);
        return scoutFile;
    }

    public void deletePersonalDataFile(Integer scoutId, Integer fileId) {
        Scout scout = scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> scoutFiles = scout.getPersonalData().getDocuments();

        ScoutFile scoutFile = scoutFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        scoutFiles.remove(scoutFile);
        scoutRepository.save(scout);

        scoutFileRepository.deleteById(fileId);
    }
}
