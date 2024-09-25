package org.scouts105bentaya.dto.attendance;

public class AttendanceScoutEventInfo {
    private Boolean attending;
    private String name;
    private Integer scoutId;
    private Boolean payed;

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
