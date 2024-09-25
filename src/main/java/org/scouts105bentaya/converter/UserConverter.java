package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.UserDto;
import org.scouts105bentaya.entity.Role;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserConverter extends GenericConverter<User, UserDto> {

    private static final String fakePassword = "fake_password";
    private final RoleRepository roleRepository;
    private final ScoutUserConverter scoutUserConverter;

    public UserConverter(RoleRepository roleRepository, ScoutUserConverter scoutUserConverter) {
        this.roleRepository = roleRepository;
        this.scoutUserConverter = scoutUserConverter;
    }

    @Override
    public User convertFromDto(UserDto dto) {
        User user = new User();
        user.setPassword(fakePassword.equals(dto.getPassword()) ? "" : dto.getPassword());
        user.setUsername(dto.getUsername().toLowerCase());
        user.setEnabled(dto.isEnabled());
        user.setId(dto.getId());
        user.setGroupId(Group.valueOf(dto.getGroupId()));
        user.setRoles(dto.getRoles().stream().map(role -> roleRepository.findByName(role).orElse(null)).collect(Collectors.toList()));
        user.setScoutList(dto.getScoutList() != null ? dto.getScoutList().stream().map(scoutUserConverter::convertFromDto).collect(Collectors.toSet()) : null);
        return user;
    }

    @Override
    public UserDto convertFromEntity(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(fakePassword);
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        userDto.setEnabled(user.isEnabled());
        userDto.setGroupId(Group.valueFrom(user.getGroupId()));
        userDto.setScoutList(user.getScoutList() != null ? user.getScoutList().stream().map(scoutUserConverter::convertFromEntity).collect(Collectors.toList()) : null);
        return userDto;
    }
}
