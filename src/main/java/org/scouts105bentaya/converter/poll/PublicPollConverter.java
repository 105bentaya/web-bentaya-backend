package org.scouts105bentaya.converter.poll;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.poll.PublicPollDto;
import org.scouts105bentaya.dto.poll.PublicPollOptionDto;
import org.scouts105bentaya.entity.Poll;
import org.scouts105bentaya.entity.PollAttachment;
import org.scouts105bentaya.security.service.RequestService;
import org.springframework.stereotype.Component;

@Component
public class PublicPollConverter extends GenericConverter<Poll, PublicPollDto> {

    private final RequestService requestService;

    public PublicPollConverter(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Poll convertFromDto(PublicPollDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public PublicPollDto convertFromEntity(Poll entity) {
        return new PublicPollDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getOptions().stream().map(option -> new PublicPollOptionDto(
                        option.getId(),
                        option.getName(),
                        option.getDescription(),
                        option.getVotes().size(),
                        option.getAttachments().stream().map(PollAttachment::getLink).toList(),
                        option.getVotes().stream().anyMatch(vote -> vote.getIp().equals(requestService.getClientIP()))
                )).toList()
        );
    }
}
