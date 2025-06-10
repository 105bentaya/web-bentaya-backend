package org.scouts105bentaya.features.user;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaRoleNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.core.security.UserHasReachedMaxLoginAttemptsException;
import org.scouts105bentaya.core.security.service.LoginAttemptService;
import org.scouts105bentaya.core.security.service.RequestService;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.dto.ChangePasswordDto;
import org.scouts105bentaya.features.user.dto.UserFormDto;
import org.scouts105bentaya.features.user.dto.UserPasswordDto;
import org.scouts105bentaya.features.user.dto.UserProfileDto;
import org.scouts105bentaya.features.user.dto.UserScoutDto;
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

import java.util.ArrayList;
import java.util.Collections;
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
    private final GroupService groupService;

    @Value("${bentaya.web.url}") String url;

    public UserService(
        UserRepository userRepository,
        @Lazy PasswordEncoder passwordEncoder,
        LoginAttemptService loginAttemptService,
        EmailService emailService,
        RoleRepository roleRepository,
        RequestService requestService,
        ScoutRepository scoutRepository, GroupService groupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.requestService = requestService;
        this.scoutRepository = scoutRepository;
        this.groupService = groupService;
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
        user
            .setUsername(formDto.username().toLowerCase())
            .setGroup(groupService.findByNullableId(formDto.groupId()))
            .setRoles(formDto.roles().stream().map(role -> roleRepository.findByName(role).orElse(null)).collect(Collectors.toList()))
            .setScoutList(Optional.ofNullable(formDto.scoutIds())
                .map(scoutRepository::findAllById)
                .map(HashSet::new)
                .orElse(null)
            );

        if (!user.hasRole(RoleEnum.ROLE_SCOUTER)) {
            user.setGroup(null);
        } else if (user.getGroup() == null) {
            throw new WebBentayaBadRequestException("El usuario debe tener una unidad asignada");
        }

        if (!user.hasRole(RoleEnum.ROLE_USER)) {
            user.setScoutList(null);
        } else if (user.getScoutList() == null) {
            throw new WebBentayaBadRequestException("El usuario debe tener educandos");
        }
    }

    public User save(UserFormDto formDto) {
        log.info("Trying to save user new with username {}", formDto.username());

        if (formDto.id() != null) throw new WebBentayaBadRequestException("El usuario no puede tener una ID asignada");
        if (formDto.password().equals(GenericConstants.FAKE_PASSWORD)) throw new WebBentayaBadRequestException("La contraseña no es válida");
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

        if (!userDB.getUsername().equals(userToUpdate.username())) {
            log.info("Trying to change user's {} username from {} to {}", id, userDB.getUsername(), userToUpdate.username());
            userRepository.findByUsername(userToUpdate.username()).ifPresent(this::handleUserAlreadyExists);
        }

        if (!userToUpdate.password().equals(GenericConstants.FAKE_PASSWORD)) userDB.setPassword(passwordEncoder.encode(userToUpdate.password()));

        updateUserBasicData(userDB, userToUpdate);
        userDB.setEnabled(true);

        return this.userRepository.save(userDB);
    }


    public void addScoutToNewUser(String username, Scout scout, RoleEnum role) {
        userRepository.findByUsername(username).ifPresent(this::handleUserAlreadyExists);

        User newUser = new User();
        newUser.setUsername(username);
        String password = PasswordHelper.generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singletonList(roleRepository.findByName(role).orElse(null)));
        newUser.setScoutList(Collections.singleton(scout));

        userRepository.save(newUser);

        //todo improve
        emailService.sendSimpleEmail(
            "Alta de usuario en la web de Asociación Scouts Exploradores Bentaya",
            String.format(
                """
                    Se ha añadido un nuevo usuario asociado a este correo para la web %s
                    Nombre de usuario: %s
                    Persona educanda asociada: %s %s
                    Contraseña: %s
                    Es altamente recomendable cambiar la contraseña.
                    Si cree que esto es un error, por favor avísenos enviando un correo a %s""",
                url, username, scout.getPersonalData().getName(), scout.getPersonalData().getSurname(), password, this.emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)[0]),
            username
        );
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

    public void addScoutToExistingUser(User user, Scout scout, RoleEnum userRole) {
        if (!user.getScoutList().contains(scout)) {
            if (!user.isEnabled()) {
                user.getRoles().removeIf(role -> role.getName() != userRole);
                user.setEnabled(true);
            }
            if (!user.hasRole(userRole)) {
                user.getRoles().add(roleRepository.findByName(userRole).orElseThrow(WebBentayaRoleNotFoundException::new));
            }
            user.getScoutList().add(scout);
            userRepository.save(user);
            emailService.sendSimpleEmail(//todo improve
                "CAMBIAR - Nueva Persona Educanda Añadida a tu usuario",
                """
                    Se ha añadido a la persona educanda %s %s a tu usuario %s de la web de la Asociación Scouts Exploradores Bentaya.
                    Si cree que esto es un error, por favor avísenos enviando un correo a %s
                    """.formatted(scout.getPersonalData().getName(), scout.getPersonalData().getSurname(), user.getUsername(), this.emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)[0]),
                user.getUsername()
            );
        }
    }

    public void removeScoutFromUser(User user, Scout scout) {
        user.getScoutList().remove(scout);
        if (user.getScoutList().isEmpty() && user.hasRole(RoleEnum.ROLE_USER) && user.getRoles().size() == 1) {
            user.setEnabled(false);
        } else if (user.getScoutList().isEmpty() && user.hasRole(RoleEnum.ROLE_USER)) {
            user.getRoles().removeIf(role -> role.getName() == RoleEnum.ROLE_USER);
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
            GroupBasicDataDto.fromGroup(user.getGroup()),
            user.getScoutList().stream().map(scout ->
                new UserScoutDto(
                    scout.getId(),
                    GroupBasicDataDto.fromGroup(scout.getGroup()),
                    scout.getPersonalData().getName(),
                    scout.getPersonalData().getSurname()
                )
            ).toList()
        );
    }

    public void delete(int id) {
        User user = findById(id);
        user.setGroup(null);
        user.setScoutList(Collections.emptySet());
        user.setEnabled(false);
        this.userRepository.save(user);
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        User user = findByUsername(SecurityUtils.getLoggedUserUsername());

        log.info("User {} with id {} trying to change password", user.getUsername(), user.getId());

        if (!changePasswordDto.newPassword().equals(changePasswordDto.newPasswordRepeat())) {
            log.warn("Passwords do not match");
            throw new WebBentayaBadRequestException("Las contraseñas nuevas no coinciden");
        }
        if (changePasswordDto.newPassword().equals(GenericConstants.FAKE_PASSWORD)) {
            log.warn("New password is not valid");
            throw new WebBentayaBadRequestException("La nueva contraseña no es válida");
        }
        if (!BCrypt.checkpw(changePasswordDto.currentPassword(), user.getPassword())) {
            log.warn("Current password is not valid");
            throw new WebBentayaBadRequestException("La contraseña actual no es válida");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    public void changeForgottenPassword(String username, String newPassword) {
        User user = findByUsername(username);

        log.info("Trying to change forgotten password for {}", username);

        if (newPassword.equals(GenericConstants.FAKE_PASSWORD)) {
            log.warn("New password is not valid");
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
