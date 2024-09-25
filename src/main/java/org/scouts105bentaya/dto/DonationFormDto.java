package org.scouts105bentaya.dto;

import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.enums.DonationFrequency;
import org.scouts105bentaya.enums.SingleDonationPaymentType;

public class DonationFormDto {

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
    private DonationFrequency frequency;

    private SingleDonationPaymentType singleDonationPaymentType;

    private String iban;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDeduct() {
        return deduct;
    }

    public void setDeduct(Boolean deduct) {
        this.deduct = deduct;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public DonationFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(DonationFrequency frequency) {
        this.frequency = frequency;
    }

    public SingleDonationPaymentType getSingleDonationPaymentType() {
        return singleDonationPaymentType;
    }

    public void setSingleDonationPaymentType(SingleDonationPaymentType singleDonationPaymentType) {
        this.singleDonationPaymentType = singleDonationPaymentType;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}