package org.scouts105bentaya.features.blog.dto;

public record BlogInfoDto(
    String title,
    String description,
    String image,
    boolean event
) {
}
