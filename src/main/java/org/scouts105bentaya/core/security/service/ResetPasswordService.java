package org.scouts105bentaya.core.security.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.scouts105bentaya.core.exception.InvalidTokenException;
import org.scouts105bentaya.core.exception.PasswordsNotMatchException;
import org.scouts105bentaya.core.exception.UserHasAlreadyChangedPasswordException;
import org.scouts105bentaya.core.exception.user.UserNotFoundException;
import org.scouts105bentaya.features.user.ForgotPasswordDto;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ResetPasswordService {

    private final Cache<String, String> tokenCache;
    private final Cache<String, Boolean> usernameHasChangedPswCache;
    private final UserService userService;
    private final EmailService emailService;

    public ResetPasswordService(
        UserService userService,
        EmailService emailService
    ) {
        this.userService = userService;
        this.emailService = emailService;
        tokenCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
        usernameHasChangedPswCache = CacheBuilder.newBuilder().expireAfterWrite(4, TimeUnit.HOURS).build();
    }

    public void requestPasswordChange(String username) {
        try {
            userService.findByUsername(username);
            if (usernameHasChangedPassword(username)) {
                throw new UserHasAlreadyChangedPasswordException(
                        "La contraseña ha sido restablecida recientemente. Vuelva a intentarlo más tarde");
            }
            String token = generateToken();
            tokenCache.put(token, username);
            emailService.sendSimpleEmail(
                username,
                "Restablecer contraseña - 105 Bentaya",
                String.format("""
                    Link para restablecer la contraseña: 105bentaya.org/reset-password/%s
                    Este link caducará en 5 minutos.
                    """, token)
            );
        } catch (UserNotFoundException ignored) {
            //ignored
        }
    }

    public void resetPassword(ForgotPasswordDto dto) {
        String username = tokenCache.getIfPresent(dto.token());
        if (username == null || usernameHasChangedPassword(username)) {
            throw new InvalidTokenException("El token es inválido o ha expirado");
        }
        if (!dto.newPassword().equals(dto.newPasswordRepeat())) {
            throw new PasswordsNotMatchException("Las contraseñas no coinciden");
        }
        tokenCache.invalidate(dto.token());
        userService.changeForgottenPassword(username, dto.newPassword());
        usernameHasChangedPswCache.put(username, true);
    }

    private boolean usernameHasChangedPassword(String username) {
        Boolean cacheResult = usernameHasChangedPswCache.getIfPresent(username);
        return Boolean.TRUE.equals(cacheResult);
    }

    private String generateToken() {
        return new BigInteger(256, new SecureRandom()).toString(32);
    }
}
