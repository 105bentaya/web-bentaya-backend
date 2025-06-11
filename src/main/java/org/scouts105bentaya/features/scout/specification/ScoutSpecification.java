package org.scouts105bentaya.features.scout.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.scouts105bentaya.features.group.Section;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ScoutSpecification implements Specification<Scout> {

    private final ScoutSpecificationFilter filter;

    public ScoutSpecification(ScoutSpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Scout> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        Path<PersonalData> personalData = root.get("personalData");
        predicates.likes(filter.getName(),
            personalData.get("name"),
            personalData.get("surname"),
            personalData.get("feltName")
        );

        if (!CollectionUtils.isEmpty(filter.getGroupIds()) || !CollectionUtils.isEmpty(filter.getGroupScoutTypes())) {
            SpecificationPredicateHelper groupPredicates = new SpecificationPredicateHelper(criteriaBuilder);
            groupPredicates.inList(root.get("group").get("id"), filter.getGroupIds());
            groupPredicates.inList(root.get("scoutType"), filter.getGroupScoutTypes());
            predicates.addPredicate(groupPredicates.getPredicatesOr());
        }

        List<Section> sections = filter.getSections();
        if (!CollectionUtils.isEmpty(sections)) {
            SpecificationPredicateHelper sectionPredicates = new SpecificationPredicateHelper(criteriaBuilder);
            sectionPredicates.inList(root.get("group").get("section"), sections);
            if (sections.contains(Section.SCOUTERS)) {
                sectionPredicates.isEqual(root.get("scoutType"), ScoutType.SCOUTER);
            } else {
                predicates.isNotEqual(root.get("scoutType"), ScoutType.SCOUTER);
            }
            if (sections.contains(Section.SCOUTSUPPORT)) {
                sectionPredicates.inList(root.get("scoutType"), List.of(ScoutType.COMMITTEE, ScoutType.MANAGER));
            }
            predicates.addPredicate(sectionPredicates.getPredicatesOr());
        }

        predicates.castedLike(root.get("census"), filter.getCensus());
        predicates.localDateBetweenFilterRange(personalData.get("birthday"), filter.getFilterDates());
        predicates.inList(personalData.get("gender"), filter.getGenders());
        predicates.like(personalData.get("idDocument").get("number"), filter.getIdDocument());
        predicates.isEqual(personalData.get("imageAuthorization"), filter.getImageAuthorization());
        predicates.inList(root.get("status"), filter.getStatuses());

        return predicates.getPredicatesAnd();
    }
}
