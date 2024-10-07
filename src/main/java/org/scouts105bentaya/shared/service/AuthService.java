package org.scouts105bentaya.shared.service;

import org.scouts105bentaya.core.exception.user.UserNotFoundException;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserRepository;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        Optional<User> user = userRepository.findByUsername(SecurityUtils.getLoggedUserUsername());
        if (user.isEmpty()) {
            log.error("Error while getting current user info");
        }
        return user.orElseThrow(UserNotFoundException::new);
    }
}
