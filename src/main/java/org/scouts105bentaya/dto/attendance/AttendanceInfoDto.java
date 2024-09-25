package org.scouts105bentaya.dto.attendance;

public class AttendanceInfoDto {

    private String name;

    private String surname;

    private Integer scoutId;

    private Boolean attending;

    private Boolean payed;

    private String text;

    private String medicalData;

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

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMedicalData() {
        return medicalData;
    }

    public void setMedicalData(String medicalData) {
        this.medicalData = medicalData;
    }

    public Integer getScoutId() {
        return scoutId;
    }

    public void setScoutId(Integer scoutId) {
        this.scoutId = scoutId;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }
}
