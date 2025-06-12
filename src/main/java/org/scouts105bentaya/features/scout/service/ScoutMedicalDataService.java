package org.scouts105bentaya.features.scout.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.InsuranceHolderForm;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.entity.InsuranceHolder;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class ScoutMedicalDataService {

    private final ScoutRepository scoutRepository;

    public ScoutMedicalDataService(ScoutRepository scoutRepository) {
        this.scoutRepository = scoutRepository;
    }

    public Scout updateMedicalData(Integer id, MedicalDataFormDto form) {
        Scout scout = scoutRepository.get(id);

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
}
