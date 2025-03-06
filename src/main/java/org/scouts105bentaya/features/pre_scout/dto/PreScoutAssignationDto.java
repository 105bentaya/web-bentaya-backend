package org.scouts105bentaya.features.pre_scout.dto;

import jakarta.annotation.Nullable;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;

import java.time.ZonedDateTime;

public record PreScoutAssignationDto(
    Integer preScoutId,
    Integer status,
    String comment,
    GroupBasicDataDto group,
    ZonedDateTime assignationDate
) {
    public static PreScoutAssignationDto ofPreScoutAssignation(@Nullable PreScoutAssignation assignation) {
        if (assignation == null) return null;
        return new PreScoutAssignationDto(
            assignation.getPreScoutId(),
            assignation.getStatus(),
            assignation.getComment(),
            GroupBasicDataDto.fromGroup(assignation.getGroup()),
            assignation.getAssignationDate()
        );
    }
}
