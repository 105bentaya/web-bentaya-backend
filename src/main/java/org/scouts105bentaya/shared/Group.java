package org.scouts105bentaya.shared;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Group {
    GRUPO(0, null),
    GARAJONAY(1, "bentaya.email.garajonay"),
    WAIGUNGA(2, "bentaya.email.waigunga"),
    BAOBAB(3, "bentaya.email.baobab"),
    AUTINDANA(4, "bentaya.email.autindana"),
    ARTETEIFAC(5, "bentaya.email.arteteifac"),
    ARIDANE(6, "bentaya.email.aridane"),
    IDAFE(7, "bentaya.email.idafe"),
    SCOUTERS(8, null);

    private final int value;
    private final String emailProperty;

    Group(int id, String mail) {
        this.value = id;
        this.emailProperty = mail;
    }

    public static Group valueOf(Integer value) {
        return value == null ? null :
            Arrays.stream(values())
                .filter(group -> group.value == value)
                .findFirst()
                .orElse(null);
    }

    public static Integer valueFrom(Group group) {
        if (group == null) return null;
        return group.getValue();
    }

    public String toTitleCase() {
        return this.name().toUpperCase().charAt(0) + this.name().substring(1).toLowerCase();
    }

    public boolean isNotUnit() {
        return this == GRUPO || this == SCOUTERS;
    }

    public boolean isUserAuthorized() {
        return this != SCOUTERS;
    }
}