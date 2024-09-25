package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.ChangePasswordDto;
import org.scouts105bentaya.dto.UserDto;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.specification.UserFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Page<User> findAll(UserFilterDto filter);
    User findById(int id);
    UserDto save(UserDto user);
    void addNewUserRoleUser(String username, Scout scout);
    String addNewScoutCenterUser(String username);
    UserDto update(User user, Integer id);
    void removeScout(User user, Scout scout);
    void addScout(User user, Scout scout);
    User findByUsername(String username);
    void delete(int id);
    void changePassword(ChangePasswordDto changePasswordDto);
    void changeForgottenPassword(String username, String newPassword);
}
