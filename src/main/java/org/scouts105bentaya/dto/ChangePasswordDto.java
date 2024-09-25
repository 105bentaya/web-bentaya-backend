package org.scouts105bentaya.dto;

import org.scouts105bentaya.constraint.ValidPassword;

public class ChangePasswordDto {

    private String currentPassword;

    @ValidPassword
    private String newPassword;

    private String newPasswordRepeat;

    public ChangePasswordDto() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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
