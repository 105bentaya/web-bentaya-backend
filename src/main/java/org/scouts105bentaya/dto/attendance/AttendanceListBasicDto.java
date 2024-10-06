package org.scouts105bentaya.dto.attendance;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class AttendanceListBasicDto {
    private Integer eventId;
    private ZonedDateTime eventStartDate;
    private ZonedDateTime eventEndDate;
    private String eventTitle;
    private int affirmativeConfirmations;
    private int negativeConfirmations;
    private int notRespondedConfirmations;
    private Integer affirmativeAndPayedConfirmations;
    private boolean eventHasPayment;
    private boolean eventIsClosed;

    public void incrementAffirmativeConfirmations() {
        this.affirmativeConfirmations++;
    }

    public void incrementNegativeConfirmations() {
        this.negativeConfirmations++;
    }

    public void incrementNotRespondedConfirmations() {
        this.notRespondedConfirmations++;
    }

    public void incrementAffirmativeAndPayedConfirmations() {
        if (this.affirmativeAndPayedConfirmations != null) {
            this.affirmativeAndPayedConfirmations++;
        } else {
            this.affirmativeAndPayedConfirmations = 1;
        }
    }
}
