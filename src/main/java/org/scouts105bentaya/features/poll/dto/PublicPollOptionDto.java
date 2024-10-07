package org.scouts105bentaya.features.poll.dto;

import java.util.List;

public record PublicPollOptionDto(
        Integer id,
        String name,
        String description,
        int votes,
        List<String> attachments,
        boolean voted) {
}
