package org.scouts105bentaya.dto;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.constraint.ValidPassword;

public class ForgotPasswordDto {

    @NotNull
    private String token;

    @NotNull
    @ValidPassword
    private String newPassword;

    @NotNull
    private String newPasswordRepeat;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeat() {
        return newPasswordRepeat;
    }

    public void setNewPasswordRepeat(String newPasswordRepeat) {
        this.newPasswordRepeat = newPasswordRepeat;
    }
}
