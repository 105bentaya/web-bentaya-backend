package org.scouts105bentaya.features.user.dto;

import org.scouts105bentaya.features.scout.dto.UserScoutDto;
import org.scouts105bentaya.features.user.role.RoleEnum;

import java.util.List;

public record UserProfileDto(
    Integer id,
    String username,
    List<RoleEnum> roles,
    UserScoutDto scouterScout,
    List<UserScoutDto> scoutList
) {
}
