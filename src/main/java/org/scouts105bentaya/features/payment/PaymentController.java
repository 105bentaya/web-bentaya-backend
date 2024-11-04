package org.scouts105bentaya.features.payment;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.payment.dto.PaymentDto;
import org.scouts105bentaya.features.payment.dto.PaymentRedsysFormDataDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/tpv")
public class PaymentController {

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
    public void updatePaymentAfterNotification(PaymentRedsysFormDataDto paymentRedsysFormDataDto, @PathVariable PaymentTypeEnum type) {
        log.info("METHOD PaymentController.updatePaymentAfterNotification --- PARAMS type: {}", type);
        this.paymentService.handleRedsysNotification(paymentRedsysFormDataDto, type);
    }
}
