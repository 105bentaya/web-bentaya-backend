package org.scouts105bentaya.dto;

import java.util.Date;
import java.util.List;

public record ScoutUserDto(
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
    List<ContactDto> contactList
) {
}
