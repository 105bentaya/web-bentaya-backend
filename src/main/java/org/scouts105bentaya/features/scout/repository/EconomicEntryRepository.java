package org.scouts105bentaya.features.scout.repository;

import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomicEntryRepository extends JpaRepository<EconomicEntry, Integer> {
}
