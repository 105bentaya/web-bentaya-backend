package org.scouts105bentaya.dto;

public record PreScoutAssignationDto(
    Integer preScoutId,
    Integer status,
    String comment,
    Integer groupId
) {
}
