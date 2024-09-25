package org.scouts105bentaya.dto;

public class PreScoutAssignationDto {

    Integer preScoutId;

    Integer status;

    String comment;

    Integer groupId;

    public Integer getPreScoutId() {
        return preScoutId;
    }

    public void setPreScoutId(Integer preScoutId) {
        this.preScoutId = preScoutId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
