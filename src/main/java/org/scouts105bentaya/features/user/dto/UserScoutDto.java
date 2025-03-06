package org.scouts105bentaya.features.user.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;

public record UserScoutDto(
    Integer id,
    GroupBasicDataDto group,
    String name,
    String surname
) {
}