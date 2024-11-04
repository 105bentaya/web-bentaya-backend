package org.scouts105bentaya.features.donation;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.donation.converter.DonationConverter;
import org.scouts105bentaya.features.donation.dto.DonationDto;
import org.scouts105bentaya.features.donation.dto.DonationFormDto;
import org.scouts105bentaya.features.payment.dto.PaymentRedsysFormDataDto;
import org.scouts105bentaya.features.payment.dto.PaymentUrlsDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/donation")
public class DonationController {

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
    public PaymentRedsysFormDataDto getPaymentAsRedsysData(@PathVariable Integer id, @RequestBody PaymentUrlsDto urls) {
        log.info("METHOD DonationController.getPaymentFormData --- PARAMS donationId: {}", id);
        return donationService.getDonationPaymentAsRedsysData(id, urls);
    }
}
