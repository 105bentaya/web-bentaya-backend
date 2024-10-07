package org.scouts105bentaya.dto;

public record ConfirmationDto(
    Integer scoutId,
    Integer eventId,
    Boolean attending,
    String text,
    Boolean payed
) {
}
