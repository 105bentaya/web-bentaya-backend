package org.scouts105bentaya.features.user.dto.form;

import org.scouts105bentaya.features.user.constraint.ValidPassword;

public record ChangePasswordDto(
    String currentPassword,
    @ValidPassword String newPassword,
    String newPasswordRepeat
) {
}
