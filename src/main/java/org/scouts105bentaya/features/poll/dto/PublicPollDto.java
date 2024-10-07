package org.scouts105bentaya.features.poll.dto;

import java.util.List;

public record PublicPollDto(Integer id, String name, String description, List<PublicPollOptionDto> options) {
}
