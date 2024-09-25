package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingFormDto {

    @Length(max = 255)
    @NotBlank
    private String groupName;

    @Length(max = 255)
    @NotBlank
    private String cif;

    @Length(max = 511)
    @NotBlank
    private String workDescription;

    @Length(max = 255)
    @NotBlank
    private String contactName;

    @Length(max = 255)
    @NotBlank
    private String relationship;

    @Length(max = 255)
    @NotBlank
    @Email
    private String email;

    @Length(max = 255)
    @NotBlank
    private String phone;

    @Positive
    @NotNull
    private Integer packs;

    @NotNull
    private ScoutCenter scoutCenter;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Length(max = 1023)
    private String observations;

    private boolean exclusiveReservation = false;
}
