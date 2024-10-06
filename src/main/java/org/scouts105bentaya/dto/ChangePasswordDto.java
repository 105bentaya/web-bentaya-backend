package org.scouts105bentaya.dto;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.constraint.ValidPassword;

@Getter
@Setter
public class ChangePasswordDto {
    private String currentPassword;
    @ValidPassword
    private String newPassword;
    private String newPasswordRepeat;
}
