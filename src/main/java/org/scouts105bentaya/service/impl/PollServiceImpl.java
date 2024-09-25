package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.entity.Poll;
import org.scouts105bentaya.entity.PollOption;
import org.scouts105bentaya.entity.PollVote;
import org.scouts105bentaya.exception.NotFoundException;
import org.scouts105bentaya.exception.WebBentayaException;
import org.scouts105bentaya.repository.PollOptionRepository;
import org.scouts105bentaya.repository.PollRepository;
import org.scouts105bentaya.repository.PollVoteRepository;
import org.scouts105bentaya.security.service.RequestService;
import org.scouts105bentaya.service.PollService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PollOptionRepository pollOptionRepository;
    private final RequestService requestService;

    public PollServiceImpl(PollRepository pollRepository, PollVoteRepository pollVoteRepository, PollOptionRepository pollOptionRepository, RequestService requestService) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.requestService = requestService;
    }

    @Override
    public List<Poll> findAll() {
        return pollRepository.findAll();
    }

    @Override
    public Poll findById(Integer id) {
        return pollRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
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

    @Override
    public void deleteVote(Integer optionId) {
        PollOption option = pollOptionRepository.findById(optionId).orElseThrow(NotFoundException::new);
        Optional<PollVote> vote = getVote(option);
        if (vote.isPresent()) {
            pollVoteRepository.delete(vote.get());
        } else {
            throw new WebBentayaException("Ya has votado esta opción");
        }
    }

    @Override
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
