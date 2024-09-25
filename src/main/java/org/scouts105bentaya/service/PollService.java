package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.Poll;

import java.util.List;

public interface PollService {

    List<Poll> findAll();

    Poll findById(Integer id);

    void vote(Integer optionId);

    void deleteVote(Integer optionId);

    void update(Poll poll);
}
