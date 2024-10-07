package org.scouts105bentaya.features.payment;

import jakarta.transaction.Transactional;
import org.scouts105bentaya.features.payment.dto.PaymentFormDataDto;
import org.scouts105bentaya.features.payment.dto.PaymentFormDataRequestDto;
import org.scouts105bentaya.features.payment.dto.PaymentInfoDto;
import org.scouts105bentaya.core.exception.WebBentayaException;
import org.scouts105bentaya.core.exception.payment.PaymentEncryptionException;
import org.scouts105bentaya.core.exception.payment.PaymentException;
import org.scouts105bentaya.core.exception.payment.PaymentNotFoundException;
import org.scouts105bentaya.core.exception.payment.UnauthorizedPaymentNotificationException;
import org.scouts105bentaya.features.donation.DonationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sis.redsys.api.ApiMacSha256;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {

    @Value("${tpv.shop.key}")
    private String key;
    @Value("${tpv.shop.id}")
    private String shopId;
    @Value("${tpv.notification.url}")
    private String notificationUrl;

    private static final DateTimeFormatter DATE_AND_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final DonationService donationService;

    public PaymentService(
        PaymentRepository paymentRepository,
        @Lazy DonationService donationService
    ) {
        this.paymentRepository = paymentRepository;
        this.donationService = donationService;
    }

    public List<Payment> findAll() {
        return this.paymentRepository.findAll();
    }

    public Payment findById(Integer id) {
        return this.paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
    }

    public Payment savePayment(Payment payment) {
        payment.setModificationDate(ZonedDateTime.now());
        return this.paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPayment(PaymentInfoDto paymentInfoDto) {
        Payment payment = new Payment();
        payment.setStatus(-1);
        payment.setAmount(paymentInfoDto.amount());
        payment.setPaymentType(paymentInfoDto.paymentType());

        Payment savedPayment = this.savePayment(payment);

        savedPayment.setOrderNumber(LocalDate.now().format(DATE_AND_TIME_FORMATTER) + payment.getId());
        savedPayment.setModificationDate(ZonedDateTime.now());

        return this.paymentRepository.save(savedPayment);
    }

    public PaymentFormDataDto getPaymentFormData(PaymentFormDataRequestDto requestDto) {
        Payment payment = requestDto.payment();

        if (payment.getStatus() != -1) {
            log.error("Payment {} has already been processed", payment.getId());
            throw new WebBentayaException("Este pago ya ha sido procesado");
        }
        try {
            ApiMacSha256 apiMacSha256 = getApiMacSha256(payment, requestDto.okUrl(), requestDto.koUrl());
            return new PaymentFormDataDto(
                "HMAC_SHA256_V1",
                apiMacSha256.createMerchantParameters(),
                apiMacSha256.createMerchantSignature(key)
            );
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException |
                 NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e
        ) {
            log.error("METHOD getPaymentFormData: Error whilst encrypting payment form info: {}", e.getMessage());
            throw new PaymentEncryptionException();
        }
    }

    //todo comments
    public void paymentConfirmation(PaymentFormDataDto response, PaymentTypeEnum type) {
        ApiMacSha256 apiMacSha256 = new ApiMacSha256();
        try {
            apiMacSha256.decodeMerchantParameters(response.Ds_MerchantParameters());
            if (apiMacSha256.createMerchantSignatureNotif(key, response.Ds_MerchantParameters()).equals(response.Ds_Signature())) {
                String order = apiMacSha256.getOrderNotif();
                Integer status = Integer.valueOf(apiMacSha256.getParameter("Ds_Response"));
                this.updatePayment(order, status, type);
            } else {
                log.warn("METHOD paymentConfirmation: response with invalid signature for parameters {} and signature {}", response.Ds_MerchantParameters(), response.Ds_Signature());
                //enviar correo
                throw new UnauthorizedPaymentNotificationException("Response with invalid signature");
            }

        } catch (InvalidAlgorithmParameterException | UnsupportedEncodingException | NoSuchPaddingException |
                 IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            log.error("METHOD paymentConfirmation: error whilst decrypting payment confirmation: {}", e.getMessage());
            //enviar correo
            throw new PaymentEncryptionException();
        } catch (Exception e) {
            log.error("METHOD paymentConfirmation: error whilst processing payment confirmation: {}", e.getMessage());
            //enviar correo
            //throw new ();
        }
    }

    private void updatePayment(String order, Integer status, PaymentTypeEnum type) {
        Payment payment = this.paymentRepository.findByOrderNumber(order).orElseThrow(PaymentNotFoundException::new);
        if (payment.getPaymentType() != type) throw new PaymentException("Payment type does not match database type");
        payment.setStatus(status);
        payment.setModificationDate(ZonedDateTime.now());
        paymentRepository.save(payment);
        if (type == PaymentTypeEnum.DONATION) {
            donationService.confirmDonationPayment(payment.getDonation());
        }
    }

    private ApiMacSha256 getApiMacSha256(Payment payment, String okUrl, String koUrl) {
        ApiMacSha256 apiMacSha256 = new ApiMacSha256();

        apiMacSha256.setParameter("DS_MERCHANT_AMOUNT", String.valueOf(payment.getAmount()));
        apiMacSha256.setParameter("DS_MERCHANT_ORDER", payment.getOrderNumber());
        apiMacSha256.setParameter("DS_MERCHANT_MERCHANTCODE", shopId);
        apiMacSha256.setParameter("DS_MERCHANT_CURRENCY", "978");
        apiMacSha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0");
        apiMacSha256.setParameter("DS_MERCHANT_TERMINAL", "1");
        apiMacSha256.setParameter("DS_MERCHANT_URLOK", okUrl);
        apiMacSha256.setParameter("DS_MERCHANT_URLKO", koUrl);
        apiMacSha256.setParameter("DS_MERCHANT_MERCHANTURL", "%s/%s".formatted(notificationUrl, payment.getPaymentType().name()));

        return apiMacSha256;
    }
}
