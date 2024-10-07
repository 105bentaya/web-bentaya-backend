package org.scouts105bentaya.shared.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getLoggedUserUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static String getLoggedUserUsernameForLog() {
        return " --- by " + getLoggedUserUsername();
    }
}
