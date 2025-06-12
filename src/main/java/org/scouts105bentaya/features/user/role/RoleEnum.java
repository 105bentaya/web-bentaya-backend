package org.scouts105bentaya.features.user.role;

public enum RoleEnum {
    ROLE_ADMIN,
    ROLE_SCOUTER,
    ROLE_USER,
    ROLE_EDITOR,
    ROLE_TRANSACTION,
    ROLE_FORM,
    ROLE_SCOUT_CENTER_REQUESTER,
    ROLE_SCOUT_CENTER_MANAGER,
    ROLE_SECRETARY;

    public boolean isScouterRole() {
        return this == ROLE_SCOUTER ||
               this == ROLE_EDITOR ||
               this == ROLE_TRANSACTION ||
               this == ROLE_FORM ||
               this == ROLE_SCOUT_CENTER_MANAGER ||
               this == ROLE_SECRETARY;
    }
}
