package org.scouts105bentaya.features.shop.dto.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartProductFormDto(
    @NotNull @PositiveOrZero Integer count,
    @NotNull Integer productSizeId
) {
}
