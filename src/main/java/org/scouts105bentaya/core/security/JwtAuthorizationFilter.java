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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final String jwt;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, String jwt) {
        super(authenticationManager);
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Optional<UsernamePasswordAuthenticationToken> authenticationToken = getAuthentication(request);
        authenticationToken.ifPresent(usernamePasswordAuthenticationToken -> SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken));
        chain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstant.TOKEN_HEADER);
        Optional<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationToken = Optional.empty();
        if ((token != null && !token.isEmpty()) && token.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            Jws<Claims> parsedToken = JwtUtils.decodeJwtToken(token, jwt);
            if (parsedToken != null) {
                String username = parsedToken.getBody().getSubject();
                if (username != null && !username.isEmpty()) {
                    List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
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
