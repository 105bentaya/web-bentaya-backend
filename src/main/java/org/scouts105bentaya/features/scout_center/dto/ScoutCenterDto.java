package org.scouts105bentaya.features.scout_center.dto;

import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;

import java.util.List;

public record ScoutCenterDto(
    Integer id,
    String name,
    String place,
    int maxCapacity,
    int minExclusiveCapacity,
    String information,
    List<String> features,
    int price,
    String icon
) {
    public static ScoutCenterDto of(ScoutCenter center) {
        return new ScoutCenterDto(
            center.getId(),
            center.getName(),
            center.getPlace(),
            center.getMaxCapacity(),
            center.getMinExclusiveCapacity(),
            center.getInformation(),
            center.getFeatures(),
            center.getPrice(),
            center.getIcon()
        );
    }
}
