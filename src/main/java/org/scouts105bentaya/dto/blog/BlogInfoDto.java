package org.scouts105bentaya.dto.blog;

public record BlogInfoDto(
    String title,
    String description,
    String image,
    boolean event
) {
}
