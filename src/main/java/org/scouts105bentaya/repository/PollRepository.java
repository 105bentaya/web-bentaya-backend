package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Integer> {
}
