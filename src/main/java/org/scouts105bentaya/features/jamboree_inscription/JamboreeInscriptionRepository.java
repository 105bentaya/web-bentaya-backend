package org.scouts105bentaya.features.jamboree_inscription;

import org.scouts105bentaya.features.jamboree_inscription.entity.JamboreeInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamboreeInscriptionRepository extends JpaRepository<JamboreeInscription, Integer> {
}
