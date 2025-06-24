package org.scouts105bentaya.features.payment;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.payment.PaymentException;
import org.scouts105bentaya.core.exception.payment.WebBentayaPaymentNotFoundException;
import org.scouts105bentaya.features.donation.DonationService;
import org.scouts105bentaya.features.payment.dto.PaymentFormDataRequestDto;
import org.scouts105bentaya.features.payment.dto.PaymentInfoDto;
import org.scouts105bentaya.features.payment.dto.PaymentRedsysFormDataDto;
import org.scouts105bentaya.features.shop.PaymentStatus;
import org.scouts105bentaya.features.shop.service.ShopPaymentService;
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

@Slf4j
@Service
public class PaymentService {
    private static final DateTimeFormatter DATE_AND_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final String TPV_VERSION = "HMAC_SHA256_V1";

    private final PaymentRepository paymentRepository;
    private final DonationService donationService;
    private final ShopPaymentService shopPaymentService;
    @Value("${tpv.shop.key}")
    private String key;
    @Value("${tpv.shop.id}")
    private String shopId;
    @Value("${tpv.notification.url}")
    private String notificationUrl;

    public PaymentService(
        PaymentRepository paymentRepository,
        @Lazy DonationService donationService,
        @Lazy ShopPaymentService shopPaymentService
    ) {
        this.paymentRepository = paymentRepository;
        this.donationService = donationService;
        this.shopPaymentService = shopPaymentService;
    }

    public List<Payment> findAll() {
        return this.paymentRepository.findAll();
    }

    public Payment findById(Integer id) {
        return this.paymentRepository.findById(id).orElseThrow(WebBentayaPaymentNotFoundException::new);
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

    public PaymentRedsysFormDataDto getPaymentAsRedsysFormData(PaymentFormDataRequestDto requestDto) {
        Payment payment = requestDto.payment();

        if (!PaymentStatus.paymentStartedOrOngoing(payment)) {
            log.warn("getPaymentAsRedsysFormData - payment {} has already been processed", payment.getId());
            throw new WebBentayaConflictException("Este pago ya ha sido procesado");
        }
        try {
            ApiMacSha256 apiMacSha256 = getApiMacSha256(payment, requestDto.okUrl(), requestDto.koUrl());
            return new PaymentRedsysFormDataDto(
                TPV_VERSION,
                apiMacSha256.createMerchantParameters(),
                apiMacSha256.createMerchantSignature(key)
            );
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException |
                 NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e
        ) {
            log.error("getPaymentFormData - error whilst encrypting payment form info: {}", e.getMessage());
            throw new WebBentayaErrorException("No se han podido generar los datos para realizar el pago");
        }
    }

    public void handleRedsysNotification(PaymentRedsysFormDataDto response, PaymentTypeEnum type) {
        ApiMacSha256 apiMacSha256 = new ApiMacSha256();
        try {
            apiMacSha256.decodeMerchantParameters(response.Ds_MerchantParameters());
            if (apiMacSha256.createMerchantSignatureNotif(key, response.Ds_MerchantParameters()).equals(response.Ds_Signature())) {
                String order = apiMacSha256.getOrderNotif();
                Integer status = Integer.valueOf(apiMacSha256.getParameter("Ds_Response"));
                this.updatePayment(order, status, type);
            } else {
                log.error("paymentConfirmation - response with invalid signature for parameters {} and signature {}", response.Ds_MerchantParameters(), response.Ds_Signature());
                throw new PaymentException();
            }

        } catch (
            InvalidAlgorithmParameterException | UnsupportedEncodingException | NoSuchPaddingException |
            IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e
        ) {
            log.error("paymentConfirmation - error whilst decrypting payment confirmation: {}", e.getMessage());
            throw new PaymentException();
        } catch (Exception e) {
            log.error("paymentConfirmation - error whilst processing payment confirmation: {}", e.getMessage());
            throw new PaymentException();
        }
    }

    private void updatePayment(String order, Integer status, PaymentTypeEnum type) {
        Payment payment = this.paymentRepository.findByOrderNumber(order).orElseThrow(WebBentayaPaymentNotFoundException::new);
        if (payment.getPaymentType() != type) {
            log.error("updatePayment - payment {} with type {} does not match type {}", payment.getId(), payment.getPaymentType(), type);
            throw new WebBentayaConflictException("Invalid payment type");
        }
        payment.setStatus(status);
        payment.setModificationDate(ZonedDateTime.now());
        paymentRepository.save(payment);
        if (type == PaymentTypeEnum.DONATION) {
            donationService.confirmDonationPayment(payment.getDonation());
        } else if (type == PaymentTypeEnum.SHOP_PURCHASE) {
            shopPaymentService.updatePaymentStatus(payment.getShopPurchase());
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
