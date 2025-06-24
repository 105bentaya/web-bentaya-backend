package org.scouts105bentaya.features.shop.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.payment.dto.PaymentRedsysFormDataDto;
import org.scouts105bentaya.features.payment.dto.PaymentUrlsDto;
import org.scouts105bentaya.features.shop.dto.ShopPurchaseInformationDto;
import org.scouts105bentaya.features.shop.dto.form.ShopPurchaseInformationFormDto;
import org.scouts105bentaya.features.shop.service.ShopPaymentService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/shop/payment")
@PreAuthorize("hasAnyRole('SCOUTER', 'USER')")
public class ShopPaymentController {
    private final ShopPaymentService shopPaymentService;

    public ShopPaymentController(ShopPaymentService shopPaymentService) {
        this.shopPaymentService = shopPaymentService;
    }

    @GetMapping("/started")
    public ShopPurchaseInformationDto getStartedPurchase() {
        log.info("getStartedPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ShopPurchaseInformationDto.fromEntity(this.shopPaymentService.getStartedPurchase());
    }

    @GetMapping("/ongoing")
    public ShopPurchaseInformationDto getOngoingPurchase() {
        log.info("getOngoingPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ShopPurchaseInformationDto.fromEntity(this.shopPaymentService.getOngoingPurchase());
    }

    @GetMapping("/continue")
    public PaymentRedsysFormDataDto continueOngoingPurchase(PaymentUrlsDto urls) {
        log.info("continueOngoingPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.shopPaymentService.continuePurchase(urls);
    }

    @PostMapping("/start")
    public void startPurchase() {
        log.info("startPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        this.shopPaymentService.startPurchase();
    }

    @PostMapping("/cancel")
    public void cancelPurchase() {
        log.info("cancelPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        this.shopPaymentService.cancelPurchaseByCurrentUser();
    }

    @PostMapping("/confirm")
    public void confirmPurchase(@Valid @RequestBody ShopPurchaseInformationFormDto dto) {
        log.info("confirmPurchase{}", SecurityUtils.getLoggedUserUsernameForLog());
        this.shopPaymentService.confirmPurchase(dto);
    }
}
