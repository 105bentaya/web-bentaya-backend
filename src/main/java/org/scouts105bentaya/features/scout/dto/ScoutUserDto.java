package org.scouts105bentaya.features.scout.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout_contact.ContactDto;

import java.util.Date;
import java.util.List;

public record ScoutUserDto(
    Integer id,
    GroupBasicDataDto group,
    String name,
    String surname,
    String dni,
    Date birthday,
    String medicalData,
    String gender,
    boolean imageAuthorization,
    String shirtSize,
    String municipality,
    Integer census,
    List<ContactDto> contactList
) {
}
