package org.scouts105bentaya.features.user.dto;

import org.scouts105bentaya.features.scout.dto.ScoutUserDto;

import java.util.List;

public record UserDto(
    Integer id,
    String username,
    String password,
    List<String> roles,
    boolean enabled,
    Integer groupId,
    List<ScoutUserDto> scoutList
) {
}
