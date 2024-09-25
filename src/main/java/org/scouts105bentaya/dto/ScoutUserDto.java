package org.scouts105bentaya.dto;

import java.util.Date;
import java.util.List;

public class ScoutUserDto {

    private Integer id;

    private Integer groupId;

    private String name;

    private String surname;

    private String dni;

    private Date birthday;

    private String medicalData;

    private String gender;

    private boolean imageAuthorization;

    private String shirtSize;

    private String municipality;

    private Integer census;

    private List<ContactDto> contactList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<ContactDto> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactDto> contactList) {
        this.contactList = contactList;
    }

    public String getMedicalData() {
        return medicalData;
    }

    public void setMedicalData(String medicalData) {
        this.medicalData = medicalData;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isImageAuthorization() {
        return imageAuthorization;
    }

    public void setImageAuthorization(boolean imageAuthorization) {
        this.imageAuthorization = imageAuthorization;
    }

    public String getShirtSize() {
        return shirtSize;
    }

    public void setShirtSize(String shirtSize) {
        this.shirtSize = shirtSize;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public Integer getCensus() {
        return census;
    }

    public void setCensus(Integer census) {
        this.census = census;
    }
}
