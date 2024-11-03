package org.scouts105bentaya.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.scouts105bentaya.core.exception.user.UserHasReachedMaxLoginAttemptsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String BAD_CREDENTIALS_MESSAGE = "Usuario o contraseña incorrectos";
    private static final String MAX_LOGIN_ATTEMPTS_MESSAGE = "Este dispositivo ha alcanzado el número máximo de intentos de inicio de sesión. Vuelva a intentarlo en varias horas.";

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final String jwt;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwt) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        setFilterProcessesUrl(SecurityConstant.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginDto loginDto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            loginDto = mapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException exception) {
            loginDto = new LoginDto();
        }

        log.info("JwtAuthenticationFilter.attemptAuthentication as {}", loginDto.getUsername());
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        UserDetails user = ((UserDetails) authResult.getPrincipal());

        List<String> roles = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        SecretKey key = Keys.hmacShaKeyFor(jwt.getBytes());
        Date expirationDate = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8));
        String token = Jwts.builder()
            .signWith(key)
            .header()
            .type(SecurityConstant.TOKEN_TYPE)
            .and()
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .claim("rol", roles)
            .compact();

        response.addHeader(SecurityConstant.TOKEN_HEADER, SecurityConstant.TOKEN_PREFIX + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("Authentication failed: {}", failed.getMessage());
        String exceptionMessage = failed.getCause() instanceof UserHasReachedMaxLoginAttemptsException ?
            MAX_LOGIN_ATTEMPTS_MESSAGE : BAD_CREDENTIALS_MESSAGE;
        ErrorResponseHandler.authErrorHandler(response, exceptionMessage);
        response.getWriter().flush();
    }
}
