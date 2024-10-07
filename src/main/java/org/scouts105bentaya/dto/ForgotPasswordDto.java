package org.scouts105bentaya.dto;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.constraint.ValidPassword;

public record ForgotPasswordDto(
    @NotNull String token,
    @ValidPassword @NotNull String newPassword,
    @NotNull String newPasswordRepeat
) {
}
