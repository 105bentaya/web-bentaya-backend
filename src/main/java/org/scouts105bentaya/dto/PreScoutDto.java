package org.scouts105bentaya.dto;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public class PreScoutDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String section;

    @NotNull
    private String birthday;

    private String age;

    @NotNull
    private String gender;

    @NotNull
    private String dni;

    private boolean hasBeenInGroup;

    private String yearAndSection;

    @NotNull
    private String medicalData;

    @NotNull
    private String parentsName;

    @NotNull
    private String parentsSurname;

    @NotNull
    private String relationship;

    @NotNull
    private String phone;

    @NotNull
    private String email;

    private String comment;

    @NotNull
    private Integer priority;

    private String priorityInfo;

    private String creationDate;

    private Integer status;

    private Integer groupId;

    private String assignationComment;

    private ZonedDateTime assignationDate;

    private String inscriptionYear;

    @NotNull
    private String size;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public boolean isHasBeenInGroup() {
        return hasBeenInGroup;
    }

    public void setHasBeenInGroup(boolean hasBeenInGroup) {
        this.hasBeenInGroup = hasBeenInGroup;
    }

    public String getYearAndSection() {
        return yearAndSection;
    }

    public void setYearAndSection(String yearAndSection) {
        this.yearAndSection = yearAndSection;
    }

    public String getMedicalData() {
        return medicalData;
    }

    public void setMedicalData(String medicalData) {
        this.medicalData = medicalData;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getAssignationComment() {
        return assignationComment;
    }

    public void setAssignationComment(String assignationComment) {
        this.assignationComment = assignationComment;
    }

    public ZonedDateTime getAssignationDate() {
        return assignationDate;
    }

    public void setAssignationDate(ZonedDateTime assignationDate) {
        this.assignationDate = assignationDate;
    }

    public String getParentsSurname() {
        return parentsSurname;
    }

    public void setParentsSurname(String parentsSurname) {
        this.parentsSurname = parentsSurname;
    }

    public String getPriorityInfo() {
        return priorityInfo;
    }

    public void setPriorityInfo(String priorityInfo) {
        this.priorityInfo = priorityInfo;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getInscriptionYear() {
        return inscriptionYear;
    }

    public void setInscriptionYear(String inscriptionYear) {
        this.inscriptionYear = inscriptionYear;
    }
}
