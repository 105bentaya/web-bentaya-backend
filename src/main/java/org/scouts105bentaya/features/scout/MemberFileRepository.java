package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.scout.entity.MemberFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberFileRepository extends JpaRepository<MemberFile, Integer> {
}
