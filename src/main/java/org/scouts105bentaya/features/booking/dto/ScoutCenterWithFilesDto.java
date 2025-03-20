package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.ScoutCenterFile;

import java.util.List;

public record ScoutCenterWithFilesDto(
    ScoutCenterDto scoutCenter,
    ScoutCenterFile rules,
    ScoutCenterFile incidencesDoc,
    ScoutCenterFile attendanceDoc,
    ScoutCenterFile mainPhoto,
    List<ScoutCenterFile> photos
) {
}
