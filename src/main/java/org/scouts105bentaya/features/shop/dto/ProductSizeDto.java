package org.scouts105bentaya.features.shop.dto;

import org.scouts105bentaya.features.shop.entity.ProductSize;

public record ProductSizeDto(
    Integer id,
    String size,
    Integer stock
) {
    public static ProductSizeDto fromEntity(ProductSize productSize) {
        return new ProductSizeDto(
            productSize.getId(),
            productSize.getSize(),
            productSize.getStock()
        );
    }
}
