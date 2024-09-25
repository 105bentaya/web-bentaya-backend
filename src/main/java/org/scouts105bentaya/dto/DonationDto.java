package org.scouts105bentaya.dto;

import org.scouts105bentaya.enums.DonationFrequency;
import org.scouts105bentaya.enums.SingleDonationPaymentType;

import java.time.ZonedDateTime;

public class DonationDto {
    private Integer id;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private String cif;
    private String phone;
    private String email;
    private Boolean deduct;
    private Integer amount;
    private DonationFrequency frequency;
    private SingleDonationPaymentType singleDonationPaymentType;
    private String iban;
    private Integer status;
    private ZonedDateTime creationDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
