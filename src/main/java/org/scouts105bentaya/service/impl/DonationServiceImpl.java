package org.scouts105bentaya.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.scouts105bentaya.converter.DonationFormConverter;
import org.scouts105bentaya.dto.DonationFormDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataDto;
import org.scouts105bentaya.dto.payment.PaymentFormDataRequestDto;
import org.scouts105bentaya.dto.payment.PaymentInfoDto;
import org.scouts105bentaya.dto.payment.PaymentUrlsDto;
import org.scouts105bentaya.entity.Donation;
import org.scouts105bentaya.entity.Payment;
import org.scouts105bentaya.enums.DonationFrequency;
import org.scouts105bentaya.enums.PaymentType;
import org.scouts105bentaya.enums.SingleDonationPaymentType;
import org.scouts105bentaya.exception.payment.PaymentException;
import org.scouts105bentaya.repository.DonationRepository;
import org.scouts105bentaya.service.DonationService;
import org.scouts105bentaya.service.EmailService;
import org.scouts105bentaya.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class DonationServiceImpl implements DonationService {

    private final DonationFormConverter donationFormConverter;
    private final DonationRepository donationRepository;
    @Value("${bentaya.email.treasury}")
    private String treasuryEmail;

    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final PaymentService paymentService;

    public DonationServiceImpl(TemplateEngine templateEngine, EmailService emailService, PaymentService paymentService, DonationFormConverter donationFormConverter, DonationRepository donationRepository) {
        this.templateEngine = templateEngine;
        this.emailService = emailService;
        this.paymentService = paymentService;
        this.donationFormConverter = donationFormConverter;
        this.donationRepository = donationRepository;
    }

    @Override
    public List<Donation> getAll() {
        return donationRepository.findAll();
    }

    @Override
    public Integer saveDonation(DonationFormDto donationForm) {
        Donation donation = donationFormConverter.convertFromDto(donationForm);
        donation.setCreationDate(ZonedDateTime.now());

        if (donationForm.getFrequency().equals(DonationFrequency.SINGLE) && donationForm.getSingleDonationPaymentType().equals(SingleDonationPaymentType.TPV)) {
            PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
            paymentInfoDto.setAmount(donationForm.getAmount());
            paymentInfoDto.setPaymentType(PaymentType.DONATION);
            donation.setPayment(this.paymentService.createPayment(paymentInfoDto));
        }

        Donation savedDonation = donationRepository.save(donation);
        this.sendDonationEmails(savedDonation);
        return donation.getId();
    }

    //TODO: i dont like this, too public
    @Override
    public PaymentFormDataDto getDonationPaymentData(Integer donationId, PaymentUrlsDto urls) {
        Payment payment = donationRepository.findById(donationId).orElseThrow(EntityNotFoundException::new).getPayment();

        if (payment.getPaymentType() != PaymentType.DONATION) {
            throw new PaymentException("Acceso al pago no permitido");
        }

        PaymentFormDataRequestDto requestDto = new PaymentFormDataRequestDto();
        requestDto.setPayment(payment);
        requestDto.setOkUrl(urls.getOkUrl());
        requestDto.setKoUrl(urls.getKoUrl());
        return this.paymentService.getPaymentFormData(requestDto);
    }

    @Override
    public void confirmDonationPayment(Donation donation) {
        Integer status = donation.getPayment().getStatus();
        this.emailService.sendSimpleEmail(
                treasuryEmail,
                "Donación por TPV completada",
                "Se ha completado la donación nº %d con el siguiente estado: %d".formatted(
                        donation.getId(),
                        status
                )
        );

        if (status >= 0 && status <= 99) {
            this.emailService.sendSimpleEmail(
                    donation.getEmail(),
                    "Donación pagada con éxito",
                    "Se ha completado el pago de su donación con nº %s".formatted(donation.getPayment().getOrderNumber())
            );
        } else {
            this.emailService.sendSimpleEmail(
                    donation.getEmail(),
                    "Error en el pago de su donación",
                    "No se ha podido completar el pago de su donación nº %s con éxito. Si cree que esto es un error, contacte con informatica@105bentaya.org".formatted(donation.getPayment().getOrderNumber())
            );
        }
    }

    private void sendDonationEmails(Donation donation) {
        Context context = new Context();
        context.setVariable("donationId", donation.getId());
        context.setVariable("name", donation.getName());
        context.setVariable("firstSurname", donation.getFirstSurname());
        context.setVariable("secondSurname", donation.getSecondSurname());
        context.setVariable("cif", donation.getCif());
        context.setVariable("phone", donation.getPhone());
        context.setVariable("email", donation.getEmail());
        context.setVariable("deduct", donation.getDeduct());
        context.setVariable("amount", amountToString(donation.getAmount()));
        context.setVariable("frequency", frequencyToString(donation.getFrequency()));
        if (donation.getFrequency().equals(DonationFrequency.SINGLE)) {
            context.setVariable("singleType", singleDonationTypeToString(donation.getSingleDonationPaymentType()));
            if (donation.getSingleDonationPaymentType().equals(SingleDonationPaymentType.MANUAL)) {
                context.setVariable("manualDonation", true);
            } else if (donation.getSingleDonationPaymentType().equals(SingleDonationPaymentType.TPV)) {
                context.setVariable("donationPaymentOrder", donation.getPayment().getOrderNumber());
            }
        }
        if (showIban(donation)) {
            context.setVariable("iban", donation.getIban());
        }

        this.emailService.sendSimpleEmailWithHtml(
                treasuryEmail,
                "Nuevo Formulario de Donación %s - nº %d".formatted(frequencyToString(donation.getFrequency()), donation.getId()),
                this.templateEngine.process("donation/new-donation.html", context)
        );
        this.emailService.sendSimpleEmailWithHtml(
                donation.getEmail(),
                "Scouts 105 Bentaya - Datos de Donación %s".formatted(frequencyToString(donation.getFrequency())),
                this.templateEngine.process("donation/donation-received.html", context)
        );
    }

    private boolean showIban(Donation donation) {
        return !donation.getFrequency().equals(DonationFrequency.SINGLE) ||
                donation.getSingleDonationPaymentType().equals(SingleDonationPaymentType.IBAN);
    }

    private String singleDonationTypeToString(SingleDonationPaymentType type) {
        return switch (type) {
            case TPV -> "Mediante TPV";
            case IBAN -> "Domiciliación con IBAN";
            case MANUAL -> "Por ingreso";
        };
    }

    private String frequencyToString(DonationFrequency frequency) {
        return switch (frequency) {
            case SINGLE -> "Puntual";
            case YEARLY -> "Anual";
            case BIANNUAL -> "Semestral";
            case QUARTERLY -> "Trimestral";
            case MONTHLY -> "Mensual";
        };
    }

    private String amountToString(int amount) {
        float formattedAmount = amount / 100f;
        return String.format("%.2f€", formattedAmount).replaceAll("\\.", ",");
    }
}
