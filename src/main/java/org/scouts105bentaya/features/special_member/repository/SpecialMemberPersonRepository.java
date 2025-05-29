package org.scouts105bentaya.features.special_member.repository;

import org.scouts105bentaya.features.special_member.entity.SpecialMemberPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecialMemberPersonRepository extends JpaRepository<SpecialMemberPerson, Integer> {

    @Query("SELECT s FROM SpecialMemberPerson s WHERE s.name LIKE :filter OR s.surname LIKE :filter or s.companyName LIKE :filter or s.idDocument.number LIKE :filter")
    List<SpecialMemberPerson> findByBasicFields(String filter);
}
