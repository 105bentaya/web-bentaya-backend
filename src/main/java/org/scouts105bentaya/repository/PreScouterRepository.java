package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.PreScouter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreScouterRepository extends JpaRepository<PreScouter, Integer> {
}
