package org.scouts105bentaya.dto;

import org.scouts105bentaya.constraint.ValidPassword;

public record ChangePasswordDto(
    String currentPassword,
    @ValidPassword String newPassword,
    String newPasswordRepeat
) {
}
