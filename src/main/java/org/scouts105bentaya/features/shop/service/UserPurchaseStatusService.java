package org.scouts105bentaya.features.shop.service;

import org.scouts105bentaya.features.shop.PaymentStatus;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.scouts105bentaya.features.shop.PaymentStatus.purchaseOngoing;
import static org.scouts105bentaya.features.shop.PaymentStatus.purchaseStarted;

@Service
public class UserPurchaseStatusService {
    private final AuthService authService;

    public UserPurchaseStatusService(AuthService authService) {
        this.authService = authService;
    }

    public boolean loggedUserHasStartedOrOngoingPurchase() {
        return userHasStartedOrOngoingPurchase(authService.getLoggedUser());
    }

    public boolean userHasStartedOrOngoingPurchase(User user) {
        return user.getShopPurchases().stream()
            .anyMatch(purchase -> purchaseStarted(purchase) || purchaseOngoing(purchase));
    }

    public boolean loggedUserHasStartedPurchase() {
        return authService.getLoggedUser().getShopPurchases().stream()
            .anyMatch(PaymentStatus::purchaseStarted);
    }

    public boolean loggedUserHasOngoingPurchase() {
        return authService.getLoggedUser().getShopPurchases().stream()
            .anyMatch(PaymentStatus::purchaseOngoing);
    }

    public Optional<ShopPurchase> getLoggedUserStartedPurchase() {
        return getUserStartedPurchase(authService.getLoggedUser());
    }

    private Optional<ShopPurchase> getUserStartedPurchase(User user) {
        return user.getShopPurchases().stream()
            .filter(PaymentStatus::purchaseStarted)
            .findAny();
    }

    public Optional<ShopPurchase> getUserStartedOrOngoingPurchase(User user) {
        return user.getShopPurchases().stream()
            .filter(purchase -> purchaseStarted(purchase) || purchaseOngoing(purchase))
            .findAny();
    }

    public Optional<ShopPurchase> getLoggedUserOngoingPurchase() {
        return authService.getLoggedUser().getShopPurchases().stream()
            .filter(PaymentStatus::purchaseOngoing)
            .findAny();
    }
}
