package org.scouts105bentaya.features.pre_scouter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreScouterRepository extends JpaRepository<PreScouter, Integer> {
}
