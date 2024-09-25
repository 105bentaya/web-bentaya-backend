package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Integer> {
}
