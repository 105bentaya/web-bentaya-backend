package org.scouts105bentaya.features.pre_scout.dto;

public record PreScoutAssignationDto(
    Integer preScoutId,
    Integer status,
    String comment,
    Integer groupId
) {
}
