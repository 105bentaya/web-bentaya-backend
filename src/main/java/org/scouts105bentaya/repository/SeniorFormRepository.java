package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.SeniorForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeniorFormRepository extends JpaRepository<SeniorForm, Integer> {
}
