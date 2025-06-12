package org.scouts105bentaya.features.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.security.service.LoginAttemptService;
import org.scouts105bentaya.core.security.service.RequestService;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.dto.form.UserFormDto;
import org.scouts105bentaya.features.user.role.RoleRepository;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private EmailService emailService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RequestService requestService;
    @Mock
    private ScoutRepository scoutRepository;

    @Test
    void shouldNotSaveWhenUserAlreadyExists() {
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        UserFormDto formDto = new UserFormDto(
            null,
            "username",
            "any password",
            List.of(),
            true,
            null,
            List.of()
        );

        Assertions.assertThatThrownBy(() -> userService.save(formDto))
            .isInstanceOf(WebBentayaConflictException.class)
            .hasMessageContaining("existe");
    }

    @Test
    void shouldNotSaveWhenIdIsSpecified() {
        UserFormDto formDto = new UserFormDto(
            1,
            "username",
            "any password",
            List.of(),
            true,
            null,
            List.of()
        );

        Assertions.assertThatThrownBy(() -> userService.save(formDto))
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageContaining("ID");
    }

    @Test
    void shouldNotSaveWhenPasswordIsInvalid() {
        UserFormDto formDto = new UserFormDto(
            null,
            "username",
            "fake_password",
            List.of(),
            true,
            null,
            List.of()
        );

        Assertions.assertThatThrownBy(() -> userService.save(formDto))
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageContaining("contraseña");
    }
}