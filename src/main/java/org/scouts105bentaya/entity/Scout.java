package org.scouts105bentaya.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.scouts105bentaya.constraint.IsUnit;
import org.scouts105bentaya.enums.Group;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Scout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @IsUnit
    @Enumerated(EnumType.ORDINAL)
    private Group groupId;

    private String name;

    private String surname;

    private String dni;

    private String medicalData;

    private String gender;

    private Date birthday;

    private boolean imageAuthorization;

    private String shirtSize;

    private String municipality;

    private Integer census;

    @Column(columnDefinition = "TEXT")
    private String progressions;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "scout", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<Contact> contactList;

    @OneToMany(mappedBy = "scout", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;

    @ManyToMany(mappedBy = "scoutList", fetch = FetchType.LAZY)
    private Set<User> userList;

    private boolean enabled;

    public Scout() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Group getGroupId() {
        return groupId;
    }

    public void setGroupId(Group groupId) {
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

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
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

    public List<Confirmation> getConfirmationList() {
        return confirmationList;
    }

    public void setConfirmationList(List<Confirmation> confirmationList) {
        this.confirmationList = confirmationList;
    }

    public Set<User> getUserList() {
        return userList;
    }

    public void setUserList(Set<User> userList) {
        this.userList = userList;
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

    public String getProgressions() {
        return progressions;
    }

    public void setProgressions(String progressions) {
        this.progressions = progressions;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Integer getCensus() {
        return census;
    }

    public void setCensus(Integer census) {
        this.census = census;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
