package org.scouts105bentaya.features.user;

import jakarta.validation.constraints.NotNull;

public record ForgotPasswordDto(
    @NotNull String token,
    @ValidPassword @NotNull String newPassword,
    @NotNull String newPasswordRepeat
) {
}
