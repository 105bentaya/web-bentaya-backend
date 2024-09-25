package org.scouts105bentaya.dto.attendance;

import java.util.List;

public class AttendanceListUserDto {

    private Integer scoutId;

    private String name;

    private String surname;

    private List<AttendanceScoutInfoDto> info;

    public Integer getScoutId() {
        return scoutId;
    }

    public void setScoutId(Integer scoutId) {
        this.scoutId = scoutId;
    }

    public List<AttendanceScoutInfoDto> getInfo() {
        return info;
    }

    public void setInfo(List<AttendanceScoutInfoDto> info) {
        this.info = info;
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
}
