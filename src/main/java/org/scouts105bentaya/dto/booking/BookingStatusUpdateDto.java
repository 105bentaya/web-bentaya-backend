package org.scouts105bentaya.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.enums.BookingStatus;

public class BookingStatusUpdateDto {

    @NotNull
    @Positive
    private Integer id;

    @NotNull
    private BookingStatus newStatus;

    @Length(max = 2047)
    private String observations;

    private Float price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BookingStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(BookingStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getObservations() {
        if (observations != null && observations.isBlank()) {
            return null;
        }
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
