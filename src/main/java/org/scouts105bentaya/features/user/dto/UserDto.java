package org.scouts105bentaya.features.user.dto;

import org.scouts105bentaya.features.scout.dto.UserScoutDto;
import org.scouts105bentaya.features.user.role.RoleEnum;

import java.util.List;

public record UserDto(
    Integer id,
    String username,
    String password,
    List<RoleEnum> roles,
    boolean enabled,
    String groupName,
    List<UserScoutDto> scoutList
) {
}
