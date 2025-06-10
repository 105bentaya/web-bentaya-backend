package org.scouts105bentaya.features.user.dto;

import jakarta.annotation.Nullable;
import org.scouts105bentaya.features.user.User;

public record UserPasswordDto(
    User user,
    @Nullable String password
) {
}