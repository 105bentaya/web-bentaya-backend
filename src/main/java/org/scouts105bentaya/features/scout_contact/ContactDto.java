package org.scouts105bentaya.features.scout_contact;

public record ContactDto(
    Integer id,
    String name,
    String relationship,
    String phone,
    String email
) {
}
