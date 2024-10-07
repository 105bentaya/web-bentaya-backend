package org.scouts105bentaya.features.donation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.payment.Payment;
import org.scouts105bentaya.features.donation.enums.DonationFrequency;
import org.scouts105bentaya.features.donation.enums.SingleDonationPaymentType;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String firstSurname;
    @NotNull
    private String secondSurname;
    @NotNull
    private String cif;
    @NotNull
    private String phone;
    @NotNull
    private String email;
    @NotNull
    private Boolean deduct;
    @NotNull
    private Integer amount;
    @NotNull
    @Enumerated(EnumType.STRING)
    private DonationFrequency frequency;
    @Enumerated(EnumType.STRING)
    private SingleDonationPaymentType singleDonationPaymentType;
    private String iban;
    private ZonedDateTime creationDate;
    @OneToOne
    @JoinTable(name = "donation_payment", joinColumns = @JoinColumn(name = "donation_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "payment_id", referencedColumnName = "id"))
    private Payment payment;
}