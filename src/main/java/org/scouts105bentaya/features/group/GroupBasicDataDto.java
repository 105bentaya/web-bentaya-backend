package org.scouts105bentaya.features.group;

import jakarta.annotation.Nullable;

import java.util.Optional;

public record GroupBasicDataDto(Integer id, String name, int order, Section section) {
    public static GroupBasicDataDto fromGroup(@Nullable Group group) {
        return Optional.ofNullable(group)
            .map(g -> new GroupBasicDataDto(g.getId(), g.getName(), g.getOrder(), g.getSection()))
            .orElse(null);
    }

    public static GroupBasicDataDto fromGroupNullAsGeneral(@Nullable Group group) {
        return Optional.ofNullable(group)
            .map(g -> new GroupBasicDataDto(g.getId(), g.getName(), g.getOrder(), g.getSection()))
            .orElse(new GroupBasicDataDto(0, "Grupo", 0, null));
    }
}
