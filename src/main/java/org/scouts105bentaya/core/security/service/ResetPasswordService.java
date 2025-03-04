package org.scouts105bentaya.core.security.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaUnauthorizedException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.features.user.dto.ForgotPasswordDto;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ResetPasswordService {

    @Value("${bentaya.web.url}") private String url;

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
                throw new WebBentayaConflictException("La contraseña ha sido restablecida recientemente. Vuelva a intentarlo en 5 minutos");
            }
            String token = generateToken();
            tokenCache.put(token, username);
            emailService.sendSimpleEmail(
                username,
                "Restablecer contraseña - 105 Bentaya",
                """
                    Link para restablecer la contraseña: %s/restablecer-clave/%s
                    Este link caducará en 5 minutos. En caso de que ya haya caducado, puede volver a solicitar el \
                    restablecimiento de la contraseña desde el portal de inicio de sesión.
                    """.formatted(url, token)
            );
        } catch (WebBentayaUserNotFoundException ignored) {
            //ignored
            log.warn("requestPasswordChange - user {} does not exist", username);
        }
    }

    public void resetPassword(ForgotPasswordDto dto) {
        String username = tokenCache.getIfPresent(dto.token());
        if (username == null || usernameHasChangedPassword(username)) {
            throw new WebBentayaUnauthorizedException("El link es inválido o ha expirado");
        }
        if (!dto.newPassword().equals(dto.newPasswordRepeat())) {
            throw new WebBentayaBadRequestException("Las contraseñas no coinciden");
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
