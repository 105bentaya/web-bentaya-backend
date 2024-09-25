package org.scouts105bentaya.dto;

import java.util.List;

public class UserDto {

    private Integer id;

    private String username;

    private String password;

    private List<String> roles;

    private boolean enabled;

    private Integer groupId;

    private List<ScoutUserDto> scoutList;

    public UserDto() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<ScoutUserDto> getScoutList() {
        return scoutList;
    }

    public void setScoutList(List<ScoutUserDto> scoutList) {
        this.scoutList = scoutList;
    }
}
