package org.scouts105bentaya.features.shop.dto;

import org.scouts105bentaya.features.shop.entity.Product;
import org.scouts105bentaya.shared.GenericConverter;

import java.util.List;

public record ProductDto(
    Integer id,
    String name,
    String description,
    Integer price,
    List<ProductSizeDto> stockList,
    String image,
    Integer totalStock
) {
    public static ProductDto fromEntity(Product entity) {
        return new ProductDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            GenericConverter.convertEntityCollectionToDtoList(entity.getStockList(), ProductSizeDto::fromEntity),
            entity.getImage().getUuid(),
            entity.getStockList().stream().reduce(0, (acc, stock) -> acc + stock.getStock(), Integer::sum)
        );
    }
}
