package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout.entity.Contact;

import java.time.LocalDate;
import java.util.List;

public record OldScoutDto(
    Integer id,
    GroupBasicDataDto group,
    String name,
    String surname,
    String dni,
    LocalDate birthday,
    String medicalData,
    String gender,
    boolean imageAuthorization,
    String shirtSize,
    String municipality,
    Integer census,
    String progressions,
    String observations,
    List<Contact> contactList,
    boolean enabled,
    boolean userAssigned
) {
}
