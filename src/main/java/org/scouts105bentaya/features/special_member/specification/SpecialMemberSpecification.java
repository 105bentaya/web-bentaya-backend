package org.scouts105bentaya.features.special_member.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.special_member.SpecialMemberRole;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberPerson;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class SpecialMemberSpecification implements Specification<SpecialMember> {

    private final SpecialMemberSpecificationFilter filter;

    public SpecialMemberSpecification(SpecialMemberSpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<SpecialMember> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        predicates.inList(root.get("role"), filter.getRoles());

        Join<SpecialMember, PersonalData> scout = root.join("scout", JoinType.LEFT).join("personalData", JoinType.LEFT);
        Join<SpecialMember, SpecialMemberPerson> person = root.join("person", JoinType.LEFT);
        predicates.likes(filter.getName(),
            scout.get("name"),
            scout.get("surname"),
            person.get("name"),
            person.get("surname"),
            person.get("companyName")
        );

        String censusFilter = filter.getCensus();
        if (StringUtils.isNotBlank(filter.getCensus())) {
            Character prefix = censusFilter.charAt(0);
            SpecialMemberRole role = SpecialMemberRole.getPrefixRole(prefix);

            if (role != null) {
                predicates.isEqual(root.get("role"), role);
                censusFilter = censusFilter.substring(1);
            }

            String cleanedCensus = censusFilter.replaceFirst("^0+", "");
            if (StringUtils.isNumeric(cleanedCensus)) {
                predicates.castedLike(root.get("roleCensus"), cleanedCensus);
            }
        }

        return predicates.getPredicatesAnd();
    }
}
