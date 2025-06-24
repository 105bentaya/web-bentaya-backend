package org.scouts105bentaya.features.shop.dto;

import org.scouts105bentaya.features.shop.entity.CartProduct;

public record CartProductSizeDto(
    Integer productSizeId,
    String size,
    Integer stock,
    Integer count
) {
    public static CartProductSizeDto fromEntity(CartProduct product) {
        return new CartProductSizeDto(
            product.getProductSize().getId(),
            product.getProductSize().getSize(),
            product.getProductSize().getStock(),
            product.getCount()
        );
    }
}
