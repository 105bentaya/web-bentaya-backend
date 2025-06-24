package org.scouts105bentaya.features.shop.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.shared.GenericConstants;

import java.util.List;

public record ProductFormDto(
    @NotNull @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String name,
    @Length(max = 1023) String description,
    @NotNull @PositiveOrZero Integer price,
    @Valid @NotEmpty List<ProductSizeFormDto> stockList
) {
}
