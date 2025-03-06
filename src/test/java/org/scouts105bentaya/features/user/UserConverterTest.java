package org.scouts105bentaya.features.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.scout.converter.ScoutUserConverter;
import org.scouts105bentaya.features.user.converter.UserConverter;
import org.scouts105bentaya.features.user.dto.UserDto;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @Mock
    ScoutUserConverter scoutUserConverter;

    @Test
    void convertFromEntity() {
        UserConverter converter = new UserConverter(scoutUserConverter);
        UserDto dto = converter.convertFromEntity(buildUser());
        org.assertj.core.api.Assertions.assertThat(dto.scoutList().getClass().getSimpleName()).isEqualTo("ArrayList");
        org.assertj.core.api.Assertions.assertThat(dto.roles().getClass().getSimpleName()).isEqualTo("ArrayList");
    }

    User buildUser() {
        User user = new User();
        user.setRoles(List.of());
        user.setScoutList(Set.of());
        return user;
    }

    UserDto buildDto() {
        return new UserDto(1, "", "", List.of(), false, "GARAJONAY", List.of());
    }
}