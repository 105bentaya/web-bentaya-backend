package org.scouts105bentaya.features.special_member.repository;

import org.scouts105bentaya.features.special_member.SpecialMemberRole;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialMemberRepository extends JpaRepository<SpecialMember, Integer>, JpaSpecificationExecutor<SpecialMember> {
    Optional<SpecialMember> findByRoleAndRoleCensus(SpecialMemberRole role, Integer roleCensus);
    Optional<SpecialMember> findByRoleAndScoutId(SpecialMemberRole role, Integer scoutId);
    Optional<SpecialMember> findByRoleAndPersonId(SpecialMemberRole role, Integer personId);
}
