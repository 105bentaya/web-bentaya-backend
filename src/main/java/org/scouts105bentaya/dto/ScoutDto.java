package org.scouts105bentaya.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ScoutDto {

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

    private String progressions;

    private String observations;

    private List<ContactDto> contactList;

    private boolean enabled;

    private boolean userAssigned;
}
