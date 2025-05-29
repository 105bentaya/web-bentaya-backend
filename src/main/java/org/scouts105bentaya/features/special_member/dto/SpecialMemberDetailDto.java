package org.scouts105bentaya.features.special_member.dto;

import org.scouts105bentaya.features.special_member.entity.SpecialMember;

import java.util.List;

public record SpecialMemberDetailDto(
    SpecialMemberPersonDto person,
    List<SpecialMemberDetailRecordDto> records
) {
    public static SpecialMemberDetailDto fromEntities(SpecialMember entity, List<SpecialMember> records) {
        return new SpecialMemberDetailDto(
            SpecialMemberPersonDto.fromEntity(entity),
            records.stream().map(SpecialMemberDetailRecordDto::fromEntity).toList()
        );
    }
}
