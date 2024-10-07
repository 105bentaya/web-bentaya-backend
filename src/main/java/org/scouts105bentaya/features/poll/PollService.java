package org.scouts105bentaya.features.poll;

import org.scouts105bentaya.core.exception.NotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaException;
import org.scouts105bentaya.core.security.service.RequestService;
import org.scouts105bentaya.features.poll.entity.Poll;
import org.scouts105bentaya.features.poll.entity.PollOption;
import org.scouts105bentaya.features.poll.entity.PollVote;
import org.scouts105bentaya.features.poll.repository.PollOptionRepository;
import org.scouts105bentaya.features.poll.repository.PollRepository;
import org.scouts105bentaya.features.poll.repository.PollVoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PollOptionRepository pollOptionRepository;
    private final RequestService requestService;

    public PollService(
        PollRepository pollRepository,
        PollVoteRepository pollVoteRepository,
        PollOptionRepository pollOptionRepository,
        RequestService requestService
    ) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.requestService = requestService;
    }

    public List<Poll> findAll() {
        return pollRepository.findAll();
    }

    public Poll findById(Integer id) {
        return pollRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void vote(Integer optionId) {
        PollOption option = pollOptionRepository.findById(optionId).orElseThrow(NotFoundException::new);
        if (!optionHasBeenVoted(option)) {
            PollVote newVote = new PollVote();
            newVote.setPollOption(option);
            newVote.setIp(requestService.getClientIP());
            pollVoteRepository.save(newVote);
        } else {
            throw new WebBentayaException("Ya has votado esta opción");
        }
    }

    public void deleteVote(Integer optionId) {
        PollOption option = pollOptionRepository.findById(optionId).orElseThrow(NotFoundException::new);
        Optional<PollVote> vote = getVote(option);
        if (vote.isPresent()) {
            pollVoteRepository.delete(vote.get());
        } else {
            throw new WebBentayaException("Ya has votado esta opción");
        }
    }

    public void update(Poll poll) {
        this.findById(poll.getId());
        poll.getOptions().forEach(option -> {
            option.setPoll(poll);
            option.getAttachments().forEach(attachment -> attachment.setPollOption(option));
        });
        pollRepository.save(poll);
    }

    private boolean optionHasBeenVoted(PollOption option) {
        return option.getVotes().stream().anyMatch(vote -> vote.getIp().equals(requestService.getClientIP()));
    }

    private Optional<PollVote> getVote(PollOption option) {
        return option.getVotes().stream()
            .filter(vote -> vote.getIp().equals(requestService.getClientIP()))
            .findAny();
    }
}
