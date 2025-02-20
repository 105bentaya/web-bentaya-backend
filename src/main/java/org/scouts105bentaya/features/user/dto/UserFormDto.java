package org.scouts105bentaya.features.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserFormDto(
    @Nullable Integer id,
    @NotBlank String username,
    @NotBlank String password,
    @NotNull @NotEmpty List<String> roles,
    @NotNull boolean enabled,
    @Nullable Integer groupId,
    @Nullable List<Integer> scoutIds
) {
}
