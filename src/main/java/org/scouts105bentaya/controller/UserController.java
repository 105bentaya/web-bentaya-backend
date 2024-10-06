package org.scouts105bentaya.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.converter.UserConverter;
import org.scouts105bentaya.dto.ChangePasswordDto;
import org.scouts105bentaya.dto.ForgotPasswordDto;
import org.scouts105bentaya.dto.UserDto;
import org.scouts105bentaya.security.service.ResetPasswordService;
import org.scouts105bentaya.service.UserService;
import org.scouts105bentaya.specification.UserFilterDto;
import org.scouts105bentaya.specification.util.PageDto;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    private final UserConverter userConverter;

    public UserController(
        UserService userService,
        ResetPasswordService resetPasswordService,
        UserConverter userConverter
    ) {
        this.userService = userService;
        this.resetPasswordService = resetPasswordService;
        this.userConverter = userConverter;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PageDto<UserDto> findAll(UserFilterDto filterDto) {
        log.info("METHOD UserController.findAll --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        return userConverter.convertEntityPageToPageDto(userService.findAll(filterDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Integer id) {
        log.info("METHOD UserController.findById --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return userConverter.convertFromEntity(userService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserDto save(@RequestBody UserDto userDto) {
        log.info("METHOD UserController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return userService.save(userDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Integer id) {
        log.info("METHOD UserController.update --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return userService.update(userConverter.convertFromDto(userDto), id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("METHOD UserController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        userService.delete(id);
    }

    @GetMapping("/me")
    public UserDto getUserInfo(Principal principal) {
        log.info("METHOD UserController.getUserInfo for {}", principal.getName());
        return userConverter.convertFromEntity(userService.findByUsername(principal.getName()));
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
