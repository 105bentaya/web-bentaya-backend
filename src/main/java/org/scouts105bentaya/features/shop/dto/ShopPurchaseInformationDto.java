package org.scouts105bentaya.features.shop.dto;

import org.scouts105bentaya.features.shop.entity.BoughtProduct;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;

import java.util.List;

public record ShopPurchaseInformationDto(
    String name,
    String surname,
    String phone,
    String email,
    String observations,
    List<BoughtProduct> boughtProducts,
    Integer amount
) {
    public static ShopPurchaseInformationDto fromEntity(ShopPurchase shopPurchase) {
        return new ShopPurchaseInformationDto(
            shopPurchase.getName(),
            shopPurchase.getSurname(),
            shopPurchase.getPhone(),
            shopPurchase.getEmail(),
            shopPurchase.getObservations(),
            shopPurchase.getBoughtProducts(),
            shopPurchase.getPayment().getAmount()
        );
    }
}
