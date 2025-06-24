package org.scouts105bentaya.features.shop;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;
import org.scouts105bentaya.features.shop.service.ShopPaymentService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.scouts105bentaya.features.shop.PaymentStatus.purchaseOngoing;

@Slf4j
@Component
public class OngoingShopPaymentScheduler {
    private final ShopPaymentService shopPaymentService;
    private final EmailService emailService;

    public OngoingShopPaymentScheduler(
        ShopPaymentService shopPaymentService,
        EmailService emailService
    ) {
        this.shopPaymentService = shopPaymentService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 */2 ? * *", zone = "Atlantic/Canary")
    private void checkingForOngoingPayments() {
        this.shopPaymentService.findAll().stream()
            .filter(purchase -> PaymentStatus.purchaseStarted(purchase) || purchaseOngoing(purchase))
            .forEach(this::checkPayment);
    }

    private void checkPayment(ShopPurchase purchase) {
        log.warn("Checking purchase with id {} from user with id {}", purchase.getId(), purchase.getUser().getId());
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(purchase.getPayment().getModificationDate(), now);
        if (duration.toHours() > 0) {
            log.warn("Trying to cancel purchase with id {} after being idle for more than one hour", purchase.getId());
            cancelPayment(purchase, duration);
        }
    }

    private void cancelPayment(ShopPurchase payment, Duration duration) {
        try {
            this.shopPaymentService.cancelPurchaseByUserId(payment.getUser().getId());
            this.emailService.sendSimpleEmail(payment.getEmail(), "Compra pendiente cancelada", """
                Buenas,
                
                Se ha cancelado su compra pendiente en www.105bentaya.org tras más de una hora de inactividad.
                
                Atentamente,
                Tienda 105 Bentaya
                """
            );
            this.emailService.sendSimpleEmail("Compra pendiente cancelada", String.format("""
                    Tienda 105 Bentaya,
                    
                    El usuario %s con id %d tiene una compra con id %d pendiente desde hace %s y se ha cancelado automáticamente
                    """, payment.getUser().getUsername(), payment.getUser().getId(), payment.getId(), duration),
                emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)
            );
        } catch (Exception e) {
            log.error("Could not cancel payment with id {} automatically: {}", payment.getId(), e.getMessage());
        }
    }
}
