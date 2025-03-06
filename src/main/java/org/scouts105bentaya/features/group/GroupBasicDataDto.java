package org.scouts105bentaya.features.group;

import jakarta.annotation.Nullable;

import java.util.Optional;

public record GroupBasicDataDto(Integer id, String name, int order) {
    public static GroupBasicDataDto fromGroup(@Nullable Group group) {
        return Optional.ofNullable(group)
            .map(g -> new GroupBasicDataDto(g.getId(), g.getName(), g.getOrder()))
            .orElse(null);
    }

    public static GroupBasicDataDto fromGroupNullAsGeneral(@Nullable Group group) {
        return Optional.ofNullable(group)
            .map(g -> new GroupBasicDataDto(g.getId(), g.getName(), g.getOrder()))
            .orElse(new GroupBasicDataDto(0, "Grupo", 0));
    }
}
