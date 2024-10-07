package org.scouts105bentaya.features.senior_section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeniorFormRepository extends JpaRepository<SeniorForm, Integer> {
}
