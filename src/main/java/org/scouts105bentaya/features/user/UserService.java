package org.scouts105bentaya.features.user;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaRoleNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.core.security.UserHasReachedMaxLoginAttemptsException;
import org.scouts105bentaya.core.security.service.LoginAttemptService;
import org.scouts105bentaya.core.security.service.RequestService;
import org.scouts105bentaya.features.scout.dto.UserScoutDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.dto.UserPasswordDto;
import org.scouts105bentaya.features.user.dto.UserProfileDto;
import org.scouts105bentaya.features.user.dto.form.ChangePasswordDto;
import org.scouts105bentaya.features.user.dto.form.UserFormDto;
import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.features.user.role.RoleRepository;
import org.scouts105bentaya.features.user.specification.UserSpecification;
import org.scouts105bentaya.features.user.specification.UserSpecificationFilter;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final RequestService requestService;
    private final ScoutRepository scoutRepository;
    private final TemplateEngine templateEngine;

    @Value("${bentaya.web.url}") String url;

    public UserService(
        UserRepository userRepository,
        @Lazy PasswordEncoder passwordEncoder,
        LoginAttemptService loginAttemptService,
        EmailService emailService,
        RoleRepository roleRepository,
        RequestService requestService,
        ScoutRepository scoutRepository,
        TemplateEngine templateEngine
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.requestService = requestService;
        this.scoutRepository = scoutRepository;
        this.templateEngine = templateEngine;
    }

    public Page<User> findAll(UserSpecificationFilter filter) {
        return userRepository.findAll(new UserSpecification(filter), filter.getPageable());
    }

    public User findById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("User with id {} could not be found", id);
        }
        return user.orElseThrow(WebBentayaUserNotFoundException::new);
    }

    public void updateUserBasicData(User user, UserFormDto formDto) {
        user.setUsername(formDto.username().toLowerCase())
            .setRoles(formDto.roles().stream().map(role -> roleRepository.findByName(role).orElse(null)).collect(Collectors.toList()));

        if (user.hasRole(RoleEnum.ROLE_SCOUTER)) {
            if (user.getScouter() == null) {
                throw new WebBentayaBadRequestException("El usuario debe tener un scouter asignado");
            }
            user.setScouter(scoutRepository.get(user.getScouter().getId()));
        } else {
            user.setScouter(null);
        }

        if (user.hasRole(RoleEnum.ROLE_USER)) {
            if (CollectionUtils.isEmpty(formDto.scoutIds())) {
                throw new WebBentayaBadRequestException("El usuario debe tener educandos");
            }
            user.setScoutList(formDto.scoutIds().stream()
                .map(scoutRepository::get)
                .collect(Collectors.toSet()));
        } else {
            user.setScoutList(null);
        }
    }

    public User save(UserFormDto formDto) {
        log.info("Trying to save user new with username {}", formDto.username());

        if (formDto.id() != null) throw new WebBentayaBadRequestException("El usuario no puede tener una ID asignada");
        if (formDto.password().equals(GenericConstants.FAKE_PASSWORD)) {
            throw new WebBentayaBadRequestException("La contraseña no es válida");
        }

        userRepository.findByUsername(formDto.username()).ifPresent(this::handleUserAlreadyExists);

        User user = new User()
            .setEnabled(true)
            .setPassword(passwordEncoder.encode(formDto.password()));

        updateUserBasicData(user, formDto);

        return this.userRepository.save(user);
    }

    public User update(UserFormDto userToUpdate, Integer id) {
        log.info("Trying to update user with id {}", id);

        User userDB = findById(id);

        if (!userDB.getUsername().equalsIgnoreCase(userToUpdate.username())) {
            log.info("Trying to change user's {} username from {} to {}", id, userDB.getUsername(), userToUpdate.username());
            userRepository.findByUsername(userToUpdate.username()).ifPresent(this::handleUserAlreadyExists);
        }

        if (!userToUpdate.password().equals(GenericConstants.FAKE_PASSWORD)) {
            userDB.setPassword(passwordEncoder.encode(userToUpdate.password()));
        }

        updateUserBasicData(userDB, userToUpdate);
        userDB.setEnabled(true);

        return this.userRepository.save(userDB);
    }

    public UserPasswordDto addNewScoutCenterUser(String username) {
        Optional<User> usernameUser = userRepository.findByUsername(username);

        if (usernameUser.isPresent()) {
            User existingUser = usernameUser.get();
            if (!existingUser.hasRole(RoleEnum.ROLE_SCOUT_CENTER_REQUESTER)) {
                existingUser.getRoles().add(roleRepository.findByName(RoleEnum.ROLE_SCOUT_CENTER_REQUESTER)
                    .orElseThrow(WebBentayaRoleNotFoundException::new)
                );
            }
            return new UserPasswordDto(existingUser, null);
        }

        User newUser = new User();
        newUser.setUsername(username);
        String password = PasswordHelper.generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singletonList(roleRepository.findByName(RoleEnum.ROLE_SCOUT_CENTER_REQUESTER).orElse(null)));

        return new UserPasswordDto(userRepository.save(newUser), password);
    }

    public void addScoutToNewUser(String username, Scout scout, RoleEnum role) {
        log.info("addScoutToNewUser - user {} does not exist, creating new user for scout {}", username, scout.getId());
        userRepository.findByUsername(username).ifPresent(this::handleUserAlreadyExists);

        User newUser = new User();
        newUser.setUsername(username);
        String password = PasswordHelper.generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singletonList(roleRepository.findByName(role).orElse(null)));

        if (role == RoleEnum.ROLE_USER) {
            newUser.setScoutList(Collections.singleton(scout));
        } else if (role == RoleEnum.ROLE_SCOUTER) {
            newUser.setScouter(scout);
        } else {
            throw new WebBentayaBadRequestException("Sólo se pueden asociar scouts a usuarios de familias o scouters");
        }

        userRepository.save(newUser);

        this.sendNewUserMail(username, scout, password);
    }

    private void sendNewUserMail(String username, Scout scout, String newUserPassword) {
        String subject = "Scouts 105 Bentaya - Alta de usuario en la Web";

        Context context = userMailBasicContext(username, scout, subject);
        context.setVariable("password", newUserPassword);

        final String htmlContent = this.templateEngine.process("users/new-user.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, htmlContent, username);
    }

    public void addScoutToExistingUser(User user, Scout scout, RoleEnum userRole) {
        log.info("addScoutToExistingUser - adding scout {} to user {}", scout.getId(), user.getUsername());
        boolean userAdded = false;
        if (userRole == RoleEnum.ROLE_USER) {
            userAdded = user.getScoutList().add(scout);
        } else if (userRole == RoleEnum.ROLE_SCOUTER) {
            if (user.getScouter() != null && !user.getScouter().getId().equals(scout.getId())) {
                throw new WebBentayaBadRequestException("Un scouter sólo puede tener un scout asociado");
            } else if (user.getScouter() == null) {
                userAdded = true;
            }
            user.setScouter(scout);
        } else {
            throw new WebBentayaBadRequestException("Sólo se pueden asociar scouts a usuarios de familias o scouters");
        }

        if (!user.isEnabled()) {
            user.getRoles().removeIf(role -> role.getName() != userRole);
            user.setEnabled(true);
            userAdded = true;
        }
        if (!user.hasRole(userRole)) {
            user.getRoles().add(roleRepository.findByName(userRole).orElseThrow(WebBentayaRoleNotFoundException::new));
        }
        userRepository.save(user);

        if (userAdded) {
            this.sendExistingUserMail(user.getUsername(), scout);
        }
    }

    private void sendExistingUserMail(String username, Scout scout) {
        String subject = "Scouts 105 Bentaya - Nueva Asociada Añadida en la Web";

        Context context = userMailBasicContext(username, scout, subject);
        final String htmlContent = this.templateEngine.process("users/existing-user.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, htmlContent, username);
    }

    private Context userMailBasicContext(String username, Scout scout, String subject) {
        Context context = new Context();

        context.setVariable("scoutName", "%s %s".formatted(scout.getPersonalData().getName(), scout.getPersonalData().getSurname()));
        context.setVariable("username", username);
        context.setVariable("webUrl", url);
        context.setVariable("itMail", emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)[0]);
        context.setVariable("subject", subject);

        return context;
    }

    public void removeScoutFromUser(User user, Scout scout) {
        if (user.getScoutList().contains(scout)) {
            log.info("removeScoutFromUser - removing scout {} from user {}", scout.getId(), user.getUsername());
            user.getScoutList().remove(scout);
            if (user.getScoutList().isEmpty() && user.hasRole(RoleEnum.ROLE_USER) && user.getRoles().size() == 1) {
                user.setEnabled(false);
            } else if (user.getScoutList().isEmpty() && user.hasRole(RoleEnum.ROLE_USER)) {
                user.getRoles().removeIf(role -> role.getName() == RoleEnum.ROLE_USER);
            }
        } else if (scout.equals(user.getScouter())) {
            log.info("removeScoutFromUser - removing scouter {} from user {}", scout.getId(), user.getUsername());
            user.setScouter(null);
            user.getRoles().removeIf(role -> role.getName().isScouterRole());
            if (user.getRoles().isEmpty()) {
                user.getRoles().add(roleRepository.findByName(RoleEnum.ROLE_SCOUTER).orElseThrow(WebBentayaRoleNotFoundException::new));
                user.setEnabled(false);
            }
        }
        if (!user.isEnabled()) {
            log.info("removeScoutFromUser - disabling user {}", user.getUsername());
        }
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(WebBentayaUserNotFoundException::new);
    }

    public UserProfileDto findProfileByUsername(String username) {
        User user = findByUsername(username);
        return new UserProfileDto(
            user.getId(),
            user.getUsername(),
            user.getRoles().stream().map(Role::getName).toList(),
            Optional.ofNullable(user.getScouter()).map(UserScoutDto::fromScout).orElse(null),
            user.getScoutList().stream().sorted(Comparator.comparing(s -> s.getPersonalData().getBirthday())).map(UserScoutDto::fromScout).toList()
        );
    }

    public void delete(int id) {
        User user = findById(id);
        user.setScouter(null);
        user.setScoutList(Collections.emptySet());
        user.setEnabled(false);
        this.userRepository.save(user);
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        User user = findByUsername(SecurityUtils.getLoggedUserUsername());

        log.info("changePassword: User {} with id {} trying to change password", user.getUsername(), user.getId());

        if (!changePasswordDto.newPassword().equals(changePasswordDto.newPasswordRepeat())) {
            log.warn("changePassword: Passwords do not match");
            throw new WebBentayaBadRequestException("Las contraseñas nuevas no coinciden");
        }
        if (changePasswordDto.newPassword().equals(GenericConstants.FAKE_PASSWORD)) {
            log.warn("changePassword: New password is not valid");
            throw new WebBentayaBadRequestException("La nueva contraseña no es válida");
        }
        if (!BCrypt.checkpw(changePasswordDto.currentPassword(), user.getPassword())) {
            log.warn("changePassword: Current password is not valid");
            throw new WebBentayaBadRequestException("La contraseña actual no es válida");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    public void changeForgottenPassword(String username, String newPassword) {
        User user = findByUsername(username);

        log.info("changeForgottenPassword: Trying to change forgotten password for {}", username);

        if (newPassword.equals(GenericConstants.FAKE_PASSWORD)) {
            log.warn("changeForgottenPassword: New password is not valid");
            throw new WebBentayaBadRequestException("La nueva contraseña no es válida");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (loginAttemptService.isBlocked()) {
            throw new UserHasReachedMaxLoginAttemptsException("Max login attempts reached by ip: %s".formatted(requestService.getClientIP()));
        }
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, buildAuthorities(user.getRoles()));
        } else {
            log.warn("The user {} wasn't found in the database", username);
            throw new UsernameNotFoundException("The user %s wasn't found in the database".formatted(username));
        }
    }

    private List<GrantedAuthority> buildAuthorities(Iterable<Role> userRoles) {
        Set<GrantedAuthority> auths = new HashSet<>();
        for (Role role : userRoles) {
            auths.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return new ArrayList<>(auths);
    }

    private void handleUserAlreadyExists(User user) {
        log.warn("User with username {} already exists", user.getUsername());
        throw new WebBentayaConflictException("Ya existe un usuario con este correo electrónico");
    }
}
