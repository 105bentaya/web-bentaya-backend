package org.scouts105bentaya.features.donation;

import org.scouts105bentaya.features.donation.dto.DonationDeclarationDto;
import org.scouts105bentaya.features.donation.dto.DonationLastTwoYearsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {
    @Query(value = """
        WITH special_member_donations AS (SELECT smd.type,
                                                 smd.amount,
                                                 COALESCE(smp.id_document_id, pd.id_document_id)                              AS id_document_id,
                                                 COALESCE(smp.type, 'REAL')                                                   as person_type,
                                                 IF(smp.id IS NOT NULL, IF(smp.type = 'JURIDICAL', smp.company_name, smp.name),
                                                    pd.name)                                                                  AS name,
                                                 IF(smp.id IS NOT NULL, IF(smp.type = 'REAL', smp.surname, null),
                                                    pd.surname)                                                               AS surname
                                          FROM special_member_donation smd
                                                   JOIN special_member sm on smd.special_member_id = sm.id
                                                   LEFT JOIN special_member_person smp on sm.person_id = smp.id
                                                   LEFT JOIN scout s on sm.scout_id = s.id
                                                   LEFT JOIN personal_data pd on s.id = pd.scout_id
                                          WHERE YEAR(smd.date) = :year),
             economic_data_donations AS (SELECT 'ECONOMIC' AS type,
                                                ee.amount,
                                                eed.id_document_id,
                                                eed.person_type,
                                                eed.name,
                                                eed.surname
                                         FROM economic_entry ee
                                                  JOIN economic_entry_donor eed ON ee.id = eed.economic_entry_id
                                         WHERE YEAR(ee.due_date) = :year),
             combined AS (SELECT *
                          FROM special_member_donations
                          UNION ALL
                          SELECT *
                          FROM economic_data_donations),
             donations AS (SELECT c.type,
                                  c.amount,
                                  id.number as id_number,
                                  c.person_type,
                                  c.name,
                                  c.surname
                           FROM combined c
                                    LEFT JOIN identification_document id on id.id = c.id_document_id),
             ranked AS (SELECT type,
                               amount,
                               person_type,
                               name,
                               surname,
                               id_number,
                               ROW_NUMBER() OVER (PARTITION BY type, id_number) AS rn
                        FROM donations)
        SELECT IF(type = 'IN_KIND', 1, 0)                                         AS isInKind,
               id_number                                                          as idNumber,
               SUM(amount)                                                        AS amount,
               MAX(CASE WHEN rn = 1 THEN IF(person_type = 'JURIDICAL', 1, 0) END) AS isJuridical,
               MAX(CASE WHEN rn = 1 THEN name END)                                AS name,
               MAX(CASE WHEN rn = 1 THEN surname END)                             AS surname
        FROM ranked
        GROUP BY type, id_number;
        """, nativeQuery = true)
    List<DonationDeclarationDto> getDonationDeclarations(int year);

    @Query(value = """
        WITH special_member_donations AS (SELECT smd.amount,
                                                 YEAR(smd.date) as year
                                          FROM special_member_donation smd
                                                   JOIN special_member sm on smd.special_member_id = sm.id
                                                   LEFT JOIN special_member_person smp on sm.person_id = smp.id
                                                   LEFT JOIN scout s on sm.scout_id = s.id
                                                   LEFT JOIN personal_data pd on s.id = pd.scout_id
                                                   JOIN identification_document id
                                                        on COALESCE(smp.id_document_id, pd.id_document_id) = id.id
                                          WHERE id.number = :idNumber),
             economic_data_donations AS (SELECT ee.amount,
                                                YEAR(ee.due_date) as date
                                         FROM economic_entry ee
                                                  JOIN economic_entry_donor eed ON ee.id = eed.economic_entry_id
                                                  JOIN identification_document id on eed.id_document_id = id.id
                                         WHERE id.number = :idNumber),
             combined AS (SELECT *
                          FROM special_member_donations
                          UNION ALL
                          SELECT *
                          FROM economic_data_donations),
             donations AS (SELECT SUM(amount) as amount,
                                  year
                           FROM combined c
                           GROUP BY year)
        SELECT COALESCE(MAX(CASE WHEN year = (:year - 1) THEN amount END), 0) AS lastYear,
               COALESCE(MAX(CASE WHEN year = (:year - 2) THEN amount END), 0) AS twoYearsBefore
        FROM donations;
        """, nativeQuery = true)
    DonationLastTwoYearsDto getLastTwoYears(String idNumber, int year);
}
