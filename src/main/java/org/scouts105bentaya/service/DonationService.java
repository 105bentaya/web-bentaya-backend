package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.DonationFormDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataDto;
import org.scouts105bentaya.dto.payment.PaymentUrlsDto;
import org.scouts105bentaya.entity.Donation;

import java.util.List;

public interface DonationService {

    List<Donation> getAll();

    Integer saveDonation(DonationFormDto donationForm);

    PaymentFormDataDto getDonationPaymentData(Integer id, PaymentUrlsDto urls);

    void confirmDonationPayment(Donation donation);
}
