package org.scouts105bentaya.features.scout_center.dto;

import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenterFile;

import java.util.List;

public record ScoutCenterWithFilesDto(
    ScoutCenterDto scoutCenter,
    ScoutCenterFile rules,
    ScoutCenterFile incidencesDoc,
    ScoutCenterFile attendanceDoc,
    ScoutCenterFile mainPhoto,
    List<ScoutCenterFile> photos
) {
    public static ScoutCenterWithFilesDto of(ScoutCenter entity) {
        return new ScoutCenterWithFilesDto(
            ScoutCenterDto.of(entity),
            entity.getRulePdf(),
            entity.getIncidencesDoc(),
            entity.getAttendanceDoc(),
            entity.getMainPhoto(),
            entity.getPhotos()
        );

    }
}
