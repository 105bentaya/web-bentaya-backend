package org.scouts105bentaya.features.pre_scouter;

public record PreScouterDto(
    Integer id,
    String name,
    String surname,
    String birthday,
    String gender,
    String phone,
    String email,
    String comment,
    String creationDate
) {
}
