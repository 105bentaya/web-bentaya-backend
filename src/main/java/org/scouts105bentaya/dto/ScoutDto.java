package org.scouts105bentaya.dto;

import java.util.Date;
import java.util.List;

public record ScoutDto(
    Integer id,
    Integer groupId,
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
    String progressions,
    String observations,
    List<ContactDto> contactList,
    boolean enabled,
    boolean userAssigned
) {
}
