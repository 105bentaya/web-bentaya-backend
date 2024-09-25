package org.scouts105bentaya.dto.poll;

import java.util.List;

public record PublicPollDto(Integer id, String name, String description, List<PublicPollOptionDto> options) {
}
