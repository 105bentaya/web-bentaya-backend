package org.scouts105bentaya.shared.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.scouts105bentaya.core.security.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    private static final Pattern BEARER = Pattern.compile("Bearer ", Pattern.LITERAL);

    private JwtUtils() {
    }

    public static Jws<Claims> decodeJwtToken(String token, String secret) throws InvalidJwtException {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(BEARER.matcher(token).replaceAll(Matcher.quoteReplacement("")));
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
        } catch (SignatureException exception) {
            log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
        } catch (ExpiredJwtException ignore) {
            //ignore
        }
        throw new InvalidJwtException();
    }
}
