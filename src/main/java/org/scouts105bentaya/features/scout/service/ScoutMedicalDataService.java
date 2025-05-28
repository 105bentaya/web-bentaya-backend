package org.scouts105bentaya.features.scout.service;

import jakarta.validation.constraints.NotNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.InsuranceHolderForm;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.entity.InsuranceHolder;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ScoutMedicalDataService {

    private final ScoutRepository scoutRepository;
    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final ScoutFileService scoutFileService;

    public ScoutMedicalDataService(
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

    public Scout updateMedicalData(Integer id, MedicalDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        MedicalData medicalData = scout.getMedicalData();
        medicalData.setFoodIntolerances(form.foodIntolerances());
        medicalData.setFoodAllergies(form.foodAllergies());
        medicalData.setFoodProblems(form.foodProblems());
        medicalData.setFoodMedication(form.foodMedication());
        medicalData.setFoodDiet(form.foodDiet());
        medicalData.setMedicalIntolerances(form.medicalIntolerances());
        medicalData.setMedicalAllergies(form.medicalAllergies());
        medicalData.setMedicalDiagnoses(form.medicalDiagnoses());
        medicalData.setMedicalPrecautions(form.medicalPrecautions());
        medicalData.setMedicalMedications(form.medicalMedications());
        medicalData.setMedicalEmergencies(form.medicalEmergencies());
        medicalData.setAddictions(form.addictions());
        medicalData.setTendencies(form.tendencies());
        medicalData.setRecords(form.records());
        medicalData.setBullyingProtocol(form.bullyingProtocol());
        medicalData.setBloodType(form.bloodType());

        if (form.socialSecurityNumber() != null) {
            medicalData.setSocialSecurityNumber(form.socialSecurityNumber());
            medicalData.setSocialSecurityHolder(form.socialSecurityHolder() != null ?
                updateInsuranceHolder(medicalData.getSocialSecurityHolder(), form.socialSecurityHolder(), scout) :
                null
            );
        } else {
            medicalData.setSocialSecurityNumber(null);
            medicalData.setSocialSecurityHolder(null);
        }

        if (form.privateInsuranceNumber() != null) {
            medicalData.setPrivateInsuranceNumber(form.privateInsuranceNumber());
            medicalData.setPrivateInsuranceEntity(form.privateInsuranceEntity());
            medicalData.setPrivateInsuranceHolder(form.privateInsuranceHolder() != null ?
                updateInsuranceHolder(medicalData.getPrivateInsuranceHolder(), form.privateInsuranceHolder(), scout)
                : null
            );
        } else {
            medicalData.setPrivateInsuranceNumber(null);
            medicalData.setPrivateInsuranceEntity(null);
            medicalData.setPrivateInsuranceHolder(null);
        }

        return scoutRepository.save(scout);
    }

    private InsuranceHolder updateInsuranceHolder(InsuranceHolder existing, InsuranceHolderForm form, Scout scout) {
        return form != null ?
            updateInsuranceHolderData(Objects.requireNonNullElseGet(existing, InsuranceHolder::new), form, scout) :
            null;
    }

    private InsuranceHolder updateInsuranceHolderData(@NotNull InsuranceHolder insuranceHolder, InsuranceHolderForm form, Scout scout) {
        if (form.contactId() != null) {
            return insuranceHolder.setContact(
                    scout.getContactList().stream()
                        .filter(contact -> contact.getId().equals(form.contactId()))
                        .findFirst().orElseThrow(WebBentayaNotFoundException::new)
                )
                .setName(null)
                .setSurname(null)
                .setEmail(null)
                .setPhone(null)
                .setIdDocument(null);
        }
        return insuranceHolder.setContact(null)
            .setName(form.name())
            .setSurname(form.surname())
            .setEmail(form.email())
            .setPhone(form.phone())
            .setIdDocument(ScoutUtils.updateIdDocument(insuranceHolder.getIdDocument(), form.idDocument()));
    }

    @Synchronized
    public ScoutFile uploadMedicalDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = scoutFileService.createScoutFile(file);

        scout.getMedicalData().getDocuments().add(scoutFile);

        scoutRepository.save(scout);
        return scoutFile;
    }

    public void deleteMedicalDataFile(Integer memberId, Integer fileId) {
        Scout scout = scoutRepository.findById(memberId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> medicalFiles = scout.getMedicalData().getDocuments();

        ScoutFile scoutFile = medicalFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        medicalFiles.remove(scoutFile);
        scoutRepository.save(scout);

        scoutFileRepository.deleteById(fileId);
    }
}
