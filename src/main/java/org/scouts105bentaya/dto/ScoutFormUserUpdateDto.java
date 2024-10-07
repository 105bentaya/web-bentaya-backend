package org.scouts105bentaya.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class ScoutFormUserUpdateDto {
    private List<String> addedUsers;
    private List<String> deletedUsers;
    private List<String> addedNewUsers;
}
