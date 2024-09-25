package org.scouts105bentaya.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.scouts105bentaya.dto.LoginDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

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

        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (InternalAuthenticationServiceException ex) {
            log.error(ex.getMessage());
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        UserDetails user = ((UserDetails) authResult.getPrincipal());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SecretKey key = Keys.hmacShaKeyFor(jwt.getBytes());
        Date expirationDate = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8));
        String token = Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", SecurityConstant.TOKEN_TYPE)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .claim("rol", roles)
                .compact();

        response.addHeader(SecurityConstant.TOKEN_HEADER, SecurityConstant.TOKEN_PREFIX + token);
    }
}
