package org.scouts105bentaya.service;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.scouts105bentaya.converter.UserConverter;
import org.scouts105bentaya.dto.ChangePasswordDto;
import org.scouts105bentaya.dto.UserDto;
import org.scouts105bentaya.entity.Role;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.enums.Roles;
import org.scouts105bentaya.exception.PasswordsNotMatchException;
import org.scouts105bentaya.exception.RoleNotFoundException;
import org.scouts105bentaya.exception.user.UserAlreadyExistsException;
import org.scouts105bentaya.exception.user.UserHasNotGroupException;
import org.scouts105bentaya.exception.user.UserHasNotScoutsException;
import org.scouts105bentaya.exception.user.UserHasReachedMaxLoginAttemptsException;
import org.scouts105bentaya.exception.user.UserNotFoundException;
import org.scouts105bentaya.repository.RoleRepository;
import org.scouts105bentaya.repository.UserRepository;
import org.scouts105bentaya.security.service.LoginAttemptService;
import org.scouts105bentaya.security.service.RequestService;
import org.scouts105bentaya.specification.UserFilterDto;
import org.scouts105bentaya.specification.UserSpecification;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final RequestService requestService;

    public UserService(
        UserRepository userRepository,
        UserConverter userConverter,
        @Lazy PasswordEncoder passwordEncoder,
        LoginAttemptService loginAttemptService,
        EmailService emailService,
        RoleRepository roleRepository,
        RequestService requestService
    ) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.requestService = requestService;
    }

    public Page<User> findAll(UserFilterDto filter) {
        return userRepository.findAll(new UserSpecification(filter), filter.getPageable());
    }

    public User findById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("User with id {} could not be found", id);
        }
        return user.orElseThrow(UserNotFoundException::new);
    }

    public UserDto save(UserDto userDto) {
        log.info("Trying to save user with username {}", userDto.getUsername());
        User user = userConverter.convertFromDto(userDto);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.error("User with username {} already exists", userDto.getUsername());
            throw new UserAlreadyExistsException("A user with this username already exists");
        }

        if (!user.hasRole(Roles.ROLE_SCOUTER)) {
            user.setGroupId(null);
        } else if (user.getGroupId() == null) {
            throw new UserHasNotGroupException("El usuario debe tener una unidad");
        }

        if (!user.hasRole(Roles.ROLE_USER)) {
            user.setScoutList(null);
        } else if (user.getScoutList() == null) {
            throw new UserHasNotScoutsException("El usuario debe tener educandos");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userConverter.convertFromEntity(this.userRepository.save(user));
    }

    public void addNewUserRoleUser(String username, Scout scout) {
        Optional<User> usernameUser = userRepository.findByUsername(username);
        if (usernameUser.isPresent()) {
            log.error("User with username {} already exists", username);
            throw new UserAlreadyExistsException("A user with this username already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        String password = generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singletonList(roleRepository.findByName(Roles.ROLE_USER.name()).orElse(null)));
        newUser.setScoutList(Collections.singleton(scout));

        userRepository.save(newUser);

        emailService.sendSimpleEmail(
            username, "Alta de usuario en la web de Asociación Scouts Exploradores Bentaya",
            String.format(
                """
                    Se ha añadido un nuevo usuario asociado a este correo para la web https://105bentaya.org
                    Nombre de usuario: %s
                    Persona educanda asociada: %s %s
                    Contraseña: %s
                    Es altamente recomendable cambiar la contraseña.
                    Si cree que esto es un error, por favor avísenos enviando un correo a informatica@105bentaya.org""",
                username, scout.getName(), scout.getSurname(), password)
        );
    }

    public String addNewScoutCenterUser(String username) {
        Optional<User> usernameUser = userRepository.findByUsername(username);

        if (usernameUser.isPresent()) {
            User existingUser = usernameUser.get();
            if (!existingUser.hasRole(Roles.ROLE_SCOUT_CENTER_REQUESTER)) {
                existingUser.getRoles().add(roleRepository.findByName("ROLE_SCOUT_CENTER_REQUESTER")
                    .orElseThrow(RoleNotFoundException::new)
                );
            }
            return null;
        }

        User newUser = new User();
        newUser.setUsername(username);
        String password = generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_SCOUT_CENTER_REQUESTER").orElse(null)));
        userRepository.save(newUser);

        return password;
    }

    private String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();

        CharacterRule lowerCase = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_LOWER";
            }

            public String getCharacters() {
                return "abcdefghijkmnopqrstuvwxyz";
            }
        }, 2);
        CharacterRule upperCase = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_UPPER";
            }

            public String getCharacters() {
                return "ABCDEFGHJKLMNPQRSTUVWXYZ";
            }
        }, 2);
        CharacterRule digit = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_DIGIT";
            }

            public String getCharacters() {
                return "123456789";
            }
        }, 2);
        CharacterRule special = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_SPECIAL";
            }

            public String getCharacters() {
                return "!_-;$%&/()";
            }
        }, 2);
        return gen.generatePassword(10, lowerCase, upperCase, digit, special);
    }

    public UserDto update(User userToUpdate, Integer id) {
        log.info("Trying to update user with id {}", id);

        User userDB = findById(id);
        Optional<User> usernameUser = userRepository.findByUsername(userToUpdate.getUsername());

        if (!userDB.getUsername().equals(userToUpdate.getUsername())) {
            log.info("Trying to change user's {} username from {} to {}", id, userDB.getUsername(), userToUpdate.getUsername());
        }
        if (usernameUser.isPresent() && !usernameUser.get().getId().equals(userDB.getId())) {
            log.error("User with username {} already exists", userToUpdate.getUsername());
            throw new UserAlreadyExistsException("A user with this username already exists");
        }
        userDB.setPassword(
            userToUpdate.getPassword() == null || userToUpdate.getPassword().isEmpty() ?
                userDB.getPassword() :
                passwordEncoder.encode(userToUpdate.getPassword())
        );
        userDB.setUsername(userToUpdate.getUsername());


        if (userDB.hasRole(Roles.ROLE_USER) && !userToUpdate.hasRole(Roles.ROLE_USER)) {
            userDB.setScoutList(null);
        }

        if (userToUpdate.hasRole(Roles.ROLE_USER)) {
            if (userToUpdate.getScoutList() == null)
                throw new UserHasNotScoutsException("El usuario debe tener educandos");
            userDB.setScoutList(userToUpdate.getScoutList());
        }

        if (userToUpdate.hasRole(Roles.ROLE_SCOUTER)) {
            if (userToUpdate.getGroupId() == null)
                throw new UserHasNotGroupException("El usuario debe tener una unidad");
            userDB.setGroupId(userToUpdate.getGroupId());
        } else {
            userDB.setGroupId(null);
        }

        userDB.setRoles(userToUpdate.getRoles());
        userDB.setEnabled(userToUpdate.isEnabled());
        userDB.setEnabled(true);

        return userConverter.convertFromEntity(this.userRepository.save(userDB));
    }

    public void addScout(User user, Scout scout) {
        if (!user.getScoutList().contains(scout)) {
            if (!user.isEnabled()) {
                user.getRoles().removeIf(role -> !role.getName().equals(Roles.ROLE_USER.name()));
                user.setEnabled(true);
            }
            if (!user.hasRole(Roles.ROLE_USER)) {
                user.getRoles().add(roleRepository.findByName(Roles.ROLE_USER.name()).orElseThrow(RoleNotFoundException::new));
            }
            user.getScoutList().add(scout);
            userRepository.save(user);
            emailService.sendSimpleEmail(
                user.getUsername(), "Nueva Persona Educanda Añadida a tu usuario",
                String.format(
                    """
                        Se ha añadido a la persona educanda %s %s a tu usuario %s de la web de la Asociación Scouts Exploradores Bentaya.
                        Si cree que esto es un error, por favor avísenos enviando un correo a informatica@105bentaya.org""",
                    scout.getName(), scout.getSurname(), user.getUsername())
            );

        }
    }

    public void removeScout(User user, Scout scout) {
        user.getScoutList().remove(scout);
        if (user.getScoutList().isEmpty() && user.hasRole(Roles.ROLE_USER) && user.getRoles().size() == 1) {
            user.setEnabled(false);
        } else if (user.getScoutList().isEmpty() && user.hasRole(Roles.ROLE_USER)) {
            user.getRoles().removeIf(role -> role.getName().equals(Roles.ROLE_USER.name()));
        }
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public void delete(int id) {
        User user = findById(id);
        user.setGroupId(null);
        user.setScoutList(Collections.emptySet());
        user.setEnabled(false);
        this.userRepository.save(user);
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {

        User user = findByUsername(SecurityUtils.getLoggedUserUsername());

        log.info("User {} with id {} trying to change password", user.getUsername(), user.getId());

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getNewPasswordRepeat())) {
            throw new PasswordsNotMatchException("Las contraseñas nuevas no coinciden");
        }
        if (changePasswordDto.getNewPassword().equals("fake_password")) {
            throw new PasswordsNotMatchException("La nueva contraseña no es válida");
        }
        if (!BCrypt.checkpw(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            log.error("Current password is not valid");
            throw new PasswordsNotMatchException("La contraseña actual no es válida");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    public void changeForgottenPassword(String username, String newPassword) {
        User user = findByUsername(username);

        log.info("Trying to change forgotten password for {}", username);

        if (newPassword.equals("fake_password")) {
            throw new PasswordsNotMatchException("La nueva contraseña no es válida");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (loginAttemptService.isBlocked()) {
            log.warn("Max login attempts reached by ip: {}", requestService.getClientIP());
            throw new UserHasReachedMaxLoginAttemptsException("Max login attempts reached");
        }
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, buildAuthorities(user.getRoles()));
        } else {
            throw new UserNotFoundException("The user " + username + " wasn't found in the database");
        }
    }

    private List<GrantedAuthority> buildAuthorities(Iterable<Role> userRoles) {
        Set<GrantedAuthority> auths = new HashSet<>();
        for (Role role : userRoles) {
            auths.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new ArrayList<>(auths);
    }
}
