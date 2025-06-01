package org.scouts105bentaya.features.user.converter;

import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.dto.UserScoutDto;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.dto.UserDto;
import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserConverter extends GenericConverter<User, UserDto> {

    @Override
    public User convertFromDto(UserDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public UserDto convertFromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            GenericConstants.FAKE_PASSWORD,
            user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
            user.isEnabled(),
            Optional.ofNullable(user.getGroup()).map(Group::getName).orElse(null),
            GenericConverter.convertEntityCollectionToDtoList(user.getScoutList(), UserScoutDto::fromScout)
        );
    }
}
