package org.scouts105bentaya.dto;

public record ContactDto(
    Integer id,
    String name,
    String relationship,
    String phone,
    String email
) {
}
