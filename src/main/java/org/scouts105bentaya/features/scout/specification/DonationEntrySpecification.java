package org.scouts105bentaya.features.scout.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class DonationEntrySpecification implements Specification<EconomicEntry> {

    private final DonationEntrySpecificationFilter filter;

    public DonationEntrySpecification(DonationEntrySpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<EconomicEntry> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        predicates.addPredicate(criteriaBuilder.equal(root.get("type"), "DONATION"));

        Path<Scout> scoutPath = root.get("economicData").get("scout");
        Path<PersonalData> personalData = scoutPath.get("personalData");
        predicates.likes(filter.getName(),
            personalData.get("name"),
            personalData.get("surname"),
            personalData.get("feltName")
        );

        predicates.castedLike(scoutPath.get("census"), filter.getCensus());
        predicates.like(personalData.get("idDocument").get("number"), filter.getIdDocument());

        predicates.localDateBetweenFilterRange(root.get("issueDate"), filter.getIssueDates());
        predicates.localDateBetweenFilterRange(root.get("dueDate"), filter.getDueDates());
        predicates.like(root.get("description"), filter.getDescription());
        predicates.inList(root.get("incomeType").get("id"), filter.getIncomeTypes());

        return predicates.getPredicatesAnd();
    }
}
