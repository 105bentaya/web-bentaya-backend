package org.scouts105bentaya.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.scouts105bentaya.shared.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String NO_AUTH_HEADER = "WEB_BENTAYA_USER_NO_LONGER_AUTHENTICATED";
    private static final String EXCEPTION_MESSAGE = "Credenciales inválidas o caducadas. Vuelva a iniciar sesión.";
    private final String jwt;
    private final AntPathMatcher matcher = new AntPathMatcher();


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, String jwt) {
        super(authenticationManager);
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Optional<UsernamePasswordAuthenticationToken> authenticationToken = this.getAuthentication(request);
            authenticationToken.ifPresent(usernamePasswordAuthenticationToken -> SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken));
            chain.doFilter(request, response);
        } catch (InvalidJwtException e) {
            if (isPublic(request)) {
                response.setHeader("Authorization", NO_AUTH_HEADER);
                chain.doFilter(request, response);
            } else {
                ErrorResponseHandler.authErrorHandler(response, EXCEPTION_MESSAGE);
                response.getWriter().flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().flush();
            logger.error(e);
        }
    }

    private boolean isPublic(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return Arrays.stream(SecurityConstant.AUTH_WHITELIST)
            .anyMatch(pattern -> matcher.match(pattern, requestUri));
    }

    private Optional<UsernamePasswordAuthenticationToken> getAuthentication(HttpServletRequest request) throws InvalidJwtException {
        String token = request.getHeader(SecurityConstant.TOKEN_HEADER);
        Optional<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationToken = Optional.empty();
        if ((token != null && !token.isEmpty()) && token.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            Jws<Claims> parsedToken = JwtUtils.decodeJwtToken(token, jwt);
            if (parsedToken != null) {
                String username = parsedToken.getPayload().getSubject();
                if (username != null && !username.isEmpty()) {
                    List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getPayload()
                        .get("rol")).stream()
                        .map(authority -> new SimpleGrantedAuthority((String) authority))
                        .toList();
                    usernamePasswordAuthenticationToken = Optional.of(new UsernamePasswordAuthenticationToken(username, null, authorities));
                }
            }
        }
        return usernamePasswordAuthenticationToken;
    }
}
