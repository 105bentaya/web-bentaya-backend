package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.exception.user.UserNotFoundException;
import org.scouts105bentaya.repository.UserRepository;
import org.scouts105bentaya.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.scouts105bentaya.util.SecurityUtils.getLoggedUserUsername;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getLoggedUser() {
        Optional<User> user = userRepository.findByUsername(getLoggedUserUsername());
        if (user.isEmpty()) {
            log.error("Error while getting current user info");
        }
        return user.orElseThrow(UserNotFoundException::new);
    }
}
