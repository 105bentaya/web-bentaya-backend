package org.scouts105bentaya.features.shop.dto.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductSizeFormDto(
    Integer id,
    @NotNull String size,
    @NotNull @PositiveOrZero Integer stock,
    Integer originalStock
) {
}
