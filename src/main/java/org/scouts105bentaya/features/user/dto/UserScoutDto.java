package org.scouts105bentaya.features.user.dto;

public record UserScoutDto(
    Integer id,
    Integer groupId,
    String name,
    String surname
) {
}