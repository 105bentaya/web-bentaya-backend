package org.scouts105bentaya.features.confirmation.dto;

public record ConfirmationDto(
    Integer scoutId,
    Integer eventId,
    Boolean attending,
    String text,
    Boolean payed
) {
}
