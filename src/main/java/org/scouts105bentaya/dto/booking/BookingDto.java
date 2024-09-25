package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.BookingStatus;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class BookingDto {

    private Integer id;

    @NotNull
    private BookingStatus status;

    @NotNull
    private ScoutCenter scoutCenter;

    @Length(max = 255)
    @NotBlank
    private String organizationName;

    @Length(max = 255)
    @NotBlank
    private String cif;

    @Length(max = 511)
    @NotBlank
    private String facilityUse;

    @Positive
    @NotNull
    private int packs;

    @Length(max = 255)
    @NotBlank
    private String contactName;

    @Length(max = 255)
    @NotBlank
    private String contactRelationship;

    @Length(max = 255)
    @NotBlank
    @Email
    private String contactMail;

    @Length(max = 255)
    @NotBlank
    private String contactPhone;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Length(max = 1023)
    private String observations;

    private String statusObservations;

    private boolean exclusiveReservation;

    private ZonedDateTime creationDate;

    private boolean userConfirmedDocuments;

    private boolean ownBooking;

    private Float price;
}
