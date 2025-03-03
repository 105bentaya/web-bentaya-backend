package org.scouts105bentaya.features.user.converter;

import org.scouts105bentaya.features.scout.converter.ScoutUserConverter;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.dto.UserDto;
import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.features.user.role.RoleRepository;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.Group;
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
        return new User()
            .setPassword(GenericConstants.FAKE_PASSWORD.equals(dto.password()) ? "" : dto.password())
            .setUsername(dto.username().toLowerCase())
            .setEnabled(dto.enabled())
            .setId(dto.id())
            .setGroupId(Group.valueOf(dto.groupId()))
            .setRoles(dto.roles().stream().map(role -> roleRepository.findByName(role).orElse(null)).collect(Collectors.toList()))
            .setScoutList(dto.scoutList() != null ? dto.scoutList().stream().map(scoutUserConverter::convertFromDto).collect(Collectors.toSet()) : null);
    }

    @Override
    public UserDto convertFromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            GenericConstants.FAKE_PASSWORD,
            user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
            user.isEnabled(),
            Group.valueFrom(user.getGroupId()),
            user.getScoutList() != null ? user.getScoutList().stream().map(scoutUserConverter::convertFromEntity).collect(Collectors.toList()) : null
        );
    }
}
