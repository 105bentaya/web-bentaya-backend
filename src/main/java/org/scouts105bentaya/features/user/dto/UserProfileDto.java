package org.scouts105bentaya.features.user.dto;

import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.user.role.RoleEnum;

import java.util.List;

public record UserProfileDto(
    Integer id,
    String username,
    List<RoleEnum> roles,
    GroupBasicDataDto scouterGroup,
    List<UserScoutDto> scoutList
) {
}
