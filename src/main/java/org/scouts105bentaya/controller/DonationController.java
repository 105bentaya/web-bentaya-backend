package org.scouts105bentaya.controller;

import jakarta.validation.Valid;
import org.scouts105bentaya.converter.DonationConverter;
import org.scouts105bentaya.dto.DonationDto;
import org.scouts105bentaya.dto.DonationFormDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataDto;
import org.scouts105bentaya.dto.payment.PaymentUrlsDto;
import org.scouts105bentaya.service.DonationService;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/donation")
public class DonationController {

    private static final Logger log = LoggerFactory.getLogger(DonationController.class);
    private final DonationService donationService;
    private final DonationConverter donationConverter;

    public DonationController(DonationService donationService, DonationConverter donationConverter) {
        this.donationService = donationService;
        this.donationConverter = donationConverter;
    }

    @PreAuthorize("hasRole('TRANSACTION')")
    @GetMapping()
    public List<DonationDto> getAll() {
        log.info("METHOD DonationController.getAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return donationConverter.convertEntityCollectionToDtoList(donationService.getAll());
    }

    @PostMapping("/public")
    public Integer newDonation(@RequestBody @Valid DonationFormDto donationFormDto) {
        log.info("METHOD DonationController.newDonation");
        return donationService.saveDonation(donationFormDto);
    }

    @PostMapping("/public/donation-data/{id}")
    public PaymentFormDataDto getPaymentFormData(@PathVariable Integer id, @RequestBody PaymentUrlsDto urls) {
        log.info("METHOD DonationController.getPaymentFormData --- PARAMS donationId: {}", id);
        return donationService.getDonationPaymentData(id, urls);
    }
}
