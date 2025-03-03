package org.scouts105bentaya.features.user.converter;

import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.dto.UserFormDto;
import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.Group;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UserFormConverter extends GenericConverter<User, UserFormDto> {

    @Override
    public User convertFromDto(UserFormDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public UserFormDto convertFromEntity(User user) {
        return new UserFormDto(
            user.getId(),
            user.getUsername(),
            GenericConstants.FAKE_PASSWORD,
            user.getRoles().stream().map(Role::getName).toList(),
            user.isEnabled(),
            Group.valueFrom(user.getGroupId()),
            Optional.ofNullable(user.getScoutList()).orElse(Collections.emptySet()).stream().map(Scout::getId).toList()
        );
    }
}
