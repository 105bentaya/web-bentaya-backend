package org.scouts105bentaya.converter;

import org.scouts105bentaya.constant.GenericConstants;
import org.scouts105bentaya.dto.UserDto;
import org.scouts105bentaya.entity.Role;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserConverter extends GenericConverter<User, UserDto> {

    private final RoleRepository roleRepository;
    private final ScoutUserConverter scoutUserConverter;

    public UserConverter(RoleRepository roleRepository, ScoutUserConverter scoutUserConverter) {
        this.roleRepository = roleRepository;
        this.scoutUserConverter = scoutUserConverter;
    }

    @Override
    public User convertFromDto(UserDto dto) {
        User user = new User();
        user.setPassword(GenericConstants.FAKE_PASSWORD.equals(dto.password()) ? "" : dto.password());
        user.setUsername(dto.username().toLowerCase());
        user.setEnabled(dto.enabled());
        user.setId(dto.id());
        user.setGroupId(Group.valueOf(dto.groupId()));
        user.setRoles(dto.roles().stream().map(role -> roleRepository.findByName(role).orElse(null)).toList());
        user.setScoutList(dto.scoutList() != null ? dto.scoutList().stream().map(scoutUserConverter::convertFromDto).collect(Collectors.toSet()) : null);
        return user;
    }

    @Override
    public UserDto convertFromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            GenericConstants.FAKE_PASSWORD,
            user.getRoles().stream().map(Role::getName).toList(),
            user.isEnabled(),
            Group.valueFrom(user.getGroupId()),
            user.getScoutList() != null ? user.getScoutList().stream().map(scoutUserConverter::convertFromEntity).toList() : null
        );
    }
}
