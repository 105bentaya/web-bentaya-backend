package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.BloodType;

public record MedicalDataFormDto(
    @Length(max = 65535) String foodIntolerances,
    @Length(max = 65535) String foodAllergies,
    @Length(max = 65535) String foodProblems,
    @Length(max = 65535) String foodDiet,
    @Length(max = 65535) String foodMedication,
    @Length(max = 65535) String medicalIntolerances,
    @Length(max = 65535) String medicalAllergies,
    @Length(max = 65535) String medicalDiagnoses,
    @Length(max = 65535) String medicalPrecautions,
    @Length(max = 65535) String medicalMedications,
    @Length(max = 65535) String medicalEmergencies,
    @Length(max = 65535) String addictions,
    @Length(max = 65535) String tendencies,
    @Length(max = 65535) String records,
    @Length(max = 65535) String bullyingProtocol,
    @NotNull BloodType bloodType,
    @Length(max = 255) String socialSecurityNumber,
    @Length(max = 255) String privateInsuranceNumber,
    @Length(max = 255) String privateInsuranceEntity,
    @Valid InsuranceHolderForm socialSecurityHolder,
    @Valid InsuranceHolderForm privateInsuranceHolder
) {
}
