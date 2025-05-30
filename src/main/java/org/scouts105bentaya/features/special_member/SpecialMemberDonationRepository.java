package org.scouts105bentaya.features.special_member;

import org.scouts105bentaya.features.special_member.entity.SpecialMemberDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialMemberDonationRepository extends JpaRepository<SpecialMemberDonation, Integer> {
}
