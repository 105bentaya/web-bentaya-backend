package org.scouts105bentaya.features.booking.dto;

import java.util.List;

public record ScoutCenterDto(
    Integer id,
    String name,
    String place,
    int maxCapacity,
    int minExclusiveCapacity,
    String information,
    List<String> features,
    int price
) {
}
