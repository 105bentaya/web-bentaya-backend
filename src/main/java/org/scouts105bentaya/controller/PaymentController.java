package org.scouts105bentaya.controller;

import org.scouts105bentaya.converter.PaymentConverter;
import org.scouts105bentaya.dto.payment.PaymentDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataDto;
import org.scouts105bentaya.enums.PaymentType;
import org.scouts105bentaya.service.PaymentService;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tpv")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final PaymentConverter paymentConverter;

    public PaymentController(
        PaymentService paymentService,
        PaymentConverter paymentConverter
    ) {
        this.paymentService = paymentService;
        this.paymentConverter = paymentConverter;
    }

    @PreAuthorize("hasRole('TRANSACTION')")
    @GetMapping
    public List<PaymentDto> findAll() {
        log.info("METHOD PaymentController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return paymentConverter.convertEntityCollectionToDtoList(paymentService.findAll());
    }

    @PostMapping("/notification/{type}")
    public void updatePaymentAfterNotification(PaymentFormDataDto paymentFormDataDto, @PathVariable PaymentType type) {
        log.info("METHOD PaymentController.updatePaymentAfterNotification --- PARAMS type: {}", type);
        this.paymentService.paymentConfirmation(paymentFormDataDto, type);
    }
}
