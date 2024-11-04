package org.scouts105bentaya.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaAuthServiceException;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserRepository;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        Optional<User> user = userRepository.findByUsername(SecurityUtils.getLoggedUserUsername());
        if (user.isEmpty()) {
            log.error("getLoggedUser - error while getting current user info");
        }
        return user.orElseThrow(WebBentayaAuthServiceException::new);
    }
}
