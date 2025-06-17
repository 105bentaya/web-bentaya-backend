package org.scouts105bentaya.features.confirmation.converter;

import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.dto.AttendanceInfoDto;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttendanceInfoConverter extends GenericConverter<Confirmation, AttendanceInfoDto> {
    @Override
    public Confirmation convertFromDto(AttendanceInfoDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public AttendanceInfoDto convertFromEntity(Confirmation entity) {
        return new AttendanceInfoDto(
            entity.getScout().getPersonalData().getName(),
            entity.getScout().getPersonalData().getSurname(),
            entity.getScout().getId(),
            entity.getAttending(),
            entity.getPayed(),
            entity.getText(),
            medicalDataFromScout(entity.getScout())
        );
    }

    private static String medicalDataFromScout(Scout scout) {
        MedicalData medicalData = scout.getMedicalData();
        List<String> medicalDataString = new ArrayList<>();
        if (medicalData.getFoodAllergies() != null) {
            medicalDataString.add("Alergias: " + medicalData.getFoodAllergies());
        }
        if (medicalData.getFoodIntolerances() != null) {
            medicalDataString.add("Intolerancias: " + medicalData.getFoodIntolerances());
        }
        if (medicalData.getFoodDiet() != null) {
            medicalDataString.add("Dietas: " + medicalData.getFoodDiet());
        }
        if (medicalData.getFoodProblems() != null) {
            medicalDataString.add("Problemas alimentarios: " + medicalData.getFoodProblems());
        }
        if (medicalData.getFoodMedication() != null) {
            medicalDataString.add("Medicación alimentaria: " + medicalData.getFoodMedication());
        }
        if (medicalDataString.isEmpty()) {
            return null;
        }
        return String.join(" - ", medicalDataString);
    }
}