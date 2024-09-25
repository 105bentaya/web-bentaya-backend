package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Integer> {
}
