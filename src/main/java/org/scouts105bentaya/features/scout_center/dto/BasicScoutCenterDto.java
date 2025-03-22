package org.scouts105bentaya.features.scout_center.dto;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;

public record BasicScoutCenterDto(int id, String name, int maxCapacity, int minExclusiveCapacity, int price) {
    public static BasicScoutCenterDto of(@NotNull ScoutCenter scoutCenter) {
        return new BasicScoutCenterDto(
            scoutCenter.getId(),
            scoutCenter.getName(),
            scoutCenter.getMaxCapacity(),
            scoutCenter.getMinExclusiveCapacity(),
            scoutCenter.getPrice()
        );
    }
}
