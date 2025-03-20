package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.ScoutCenter;
import org.scouts105bentaya.features.booking.entity.ScoutCenterFile;

import java.util.List;

public record ScoutCenterInformationDto(
    Integer id,
    String name,
    String place,
    int maxCapacity,
    int minExclusiveCapacity,
    String information,
    List<String> features,
    int price,
    List<ScoutCenterFile> photos,
    ScoutCenterFile mainPhoto,
    String icon
) {
    public static ScoutCenterInformationDto of(ScoutCenter center) {
        return new ScoutCenterInformationDto(
            center.getId(),
            center.getName(),
            center.getPlace(),
            center.getMaxCapacity(),
            center.getMinExclusiveCapacity(),
            center.getInformation(),
            center.getFeatures(),
            center.getPrice(),
            center.getPhotos(),
            center.getMainPhoto(),
            center.getIcon()
        );
    }
}
