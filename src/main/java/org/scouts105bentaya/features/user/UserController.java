package org.scouts105bentaya.features.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.security.service.ResetPasswordService;
import org.scouts105bentaya.features.user.converter.UserConverter;
import org.scouts105bentaya.features.user.converter.UserFormConverter;
import org.scouts105bentaya.features.user.dto.UserDto;
import org.scouts105bentaya.features.user.dto.UserProfileDto;
import org.scouts105bentaya.features.user.dto.form.ChangePasswordDto;
import org.scouts105bentaya.features.user.dto.form.ForgotPasswordDto;
import org.scouts105bentaya.features.user.dto.form.UserFormDto;
import org.scouts105bentaya.features.user.specification.UserSpecificationFilter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    private final UserConverter userConverter;
    private final UserFormConverter userFormConverter;

    public UserController(
        UserService userService,
        ResetPasswordService resetPasswordService,
        UserConverter userConverter,
        UserFormConverter userFormConverter) {
        this.userService = userService;
        this.resetPasswordService = resetPasswordService;
        this.userConverter = userConverter;
        this.userFormConverter = userFormConverter;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PageDto<UserDto> findAll(UserSpecificationFilter filterDto) {
        log.info("METHOD UserController.findAll --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        return userConverter.convertEntityPageToPageDto(userService.findAll(filterDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/form/{id}")
    public UserFormDto findFormDtoById(@PathVariable Integer id) {
        log.info("METHOD UserController.findFormDtoById --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return userFormConverter.convertFromEntity(userService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserFormDto save(@RequestBody @Valid UserFormDto formDto) {
        log.info("METHOD UserController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return userFormConverter.convertFromEntity(userService.save(formDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserFormDto update(@RequestBody @Valid UserFormDto formDto, @PathVariable Integer id) {
        log.info("METHOD UserController.update --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return userFormConverter.convertFromEntity(userService.update(formDto, id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("METHOD UserController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        userService.delete(id);
    }

    @GetMapping("/me")
    public UserProfileDto getUserInfo(Principal principal) {
        log.info("METHOD UserController.getUserInfo for {}", principal.getName());
        return userService.findProfileByUsername(principal.getName());
    }

    @PostMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        log.info("METHOD UserController.changePassword{}", SecurityUtils.getLoggedUserUsernameForLog());
        userService.changePassword(changePasswordDto);
    }

    @GetMapping("/password/forgot")
    public void forgotPassword(@NotNull @RequestParam String username) {
        log.info("METHOD UserController.forgotPassword --- PARAMS username: {}", username);
        resetPasswordService.requestPasswordChange(username);
    }

    @PostMapping("/password/reset")
    public void resetPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        log.info("METHOD UserController.resetPassword");
        resetPasswordService.resetPassword(forgotPasswordDto);
    }
}
