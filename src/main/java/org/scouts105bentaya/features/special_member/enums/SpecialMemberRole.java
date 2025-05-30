package org.scouts105bentaya.features.special_member.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SpecialMemberRole {
    FOUNDER('F'),
    HONOUR('H'),
    ACKNOWLEDGEMENT('R'),
    PROTECTOR('P'),
    DONOR('D');

    private final char prefix;

    SpecialMemberRole(char prefix) {
        this.prefix = prefix;
    }

    public static SpecialMemberRole getPrefixRole(Character prefix) {
        return Arrays.stream(SpecialMemberRole.values())
            .filter(role -> role.prefix == prefix)
            .findFirst().orElse(null);
    }
}
