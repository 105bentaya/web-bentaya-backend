package org.scouts105bentaya.features.shop;

import lombok.Getter;
import org.scouts105bentaya.features.payment.Payment;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;

@Getter
public enum PaymentStatus {
    CANCELED_BEFORE_PAYMENT(-3), ONGOING(-2), STARTED(-1);

    private final int code;

    PaymentStatus(int code) {
        this.code = code;
    }

    public static boolean paymentStartedOrOngoing(Payment payment) {
        return payment.getStatus().equals(STARTED.getCode()) || payment.getStatus().equals(ONGOING.getCode());
    }

    public static boolean purchaseStarted(ShopPurchase purchase) {
        return purchase.getPayment().getStatus().equals(STARTED.getCode());
    }

    public static boolean purchaseOngoing(ShopPurchase purchase) {
        return purchase.getPayment().getStatus().equals(ONGOING.getCode());
    }

    public static boolean purchaseSuccessful(ShopPurchase purchase) {
        return purchase.getPayment().getStatus() >= 0 && purchase.getPayment().getStatus() <= 99;
    }
}
