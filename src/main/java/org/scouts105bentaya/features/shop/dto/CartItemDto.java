package org.scouts105bentaya.features.shop.dto;

import java.util.List;

public record CartItemDto(
    ProductDto product,
    Integer totalPrice,
    List<CartProductSizeDto> items
) {
}
