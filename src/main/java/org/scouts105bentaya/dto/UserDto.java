package org.scouts105bentaya.dto;

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
