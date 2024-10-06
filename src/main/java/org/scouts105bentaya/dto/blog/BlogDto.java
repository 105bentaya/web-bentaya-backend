package org.scouts105bentaya.dto.blog;

import java.time.ZonedDateTime;

public record BlogDto(
    Integer id,
    String title,
    String description,
    String image,
    String data,
    ZonedDateTime modificationDate,
    ZonedDateTime endDate,
    boolean event,
    boolean published
) {
}
