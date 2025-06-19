package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.enums.PersonType;

public record ScoutDonorDto(
    String name,
    String surname,
    IdentificationDocument idDocument,
    PersonType personType
) {
}
