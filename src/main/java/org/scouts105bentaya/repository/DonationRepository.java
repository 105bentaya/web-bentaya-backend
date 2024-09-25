package org.scouts105bentaya.repository;

import org.scouts105bentaya.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {
}
