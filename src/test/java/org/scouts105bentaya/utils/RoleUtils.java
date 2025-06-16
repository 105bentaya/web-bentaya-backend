package org.scouts105bentaya.utils;

import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.features.user.role.RoleEnum;

public class RoleUtils {

    public static Role of(RoleEnum roleEnum) {
        var role = new Role();
        role.setName(roleEnum);
        return role;
    }
}
