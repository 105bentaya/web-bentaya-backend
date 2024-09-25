package org.scouts105bentaya.dto;

import java.util.List;

public class ScoutFormUserUpdateDto {

    List<String> addedUsers;

    List<String> deletedUsers;

    List<String> addedNewUsers;

    public List<String> getAddedUsers() {
        return addedUsers;
    }

    public void setAddedUsers(List<String> addedUsers) {
        this.addedUsers = addedUsers;
    }

    public List<String> getDeletedUsers() {
        return deletedUsers;
    }

    public void setDeletedUsers(List<String> deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    public List<String> getAddedNewUsers() {
        return addedNewUsers;
    }

    public void setAddedNewUsers(List<String> addedNewUsers) {
        this.addedNewUsers = addedNewUsers;
    }
}
