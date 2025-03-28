package org.scouts105bentaya.features.scout_center.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;

import java.util.List;

public record ScoutCenterDto(
    Integer id,
    @NotNull String name,
    @NotNull String place,
    @NotNull @Min(1) int maxCapacity,
    @NotNull int minExclusiveCapacity,
    @NotNull String information,
    @NotNull @NotEmpty List<String> features,
    @NotNull int price,
    @NotNull String icon,
    @NotNull String color
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
            center.getIcon(),
            center.getColor()
        );
    }
}
