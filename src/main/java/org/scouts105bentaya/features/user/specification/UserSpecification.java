package org.scouts105bentaya.features.user.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements Specification<User> {

    private final UserSpecificationFilter userSpecificationFilter;

    public UserSpecification(UserSpecificationFilter userSpecificationFilter) {
        this.userSpecificationFilter = userSpecificationFilter;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        predicates.like(root.get("username"), userSpecificationFilter.getDescription());
        predicates.inList(root.get("roles").get("id"), userSpecificationFilter.getRoleIds());

        if (!userSpecificationFilter.isShowHidden()) {
            predicates.addPredicate(criteriaBuilder.isTrue(root.get("enabled")));
        }

        return predicates.getPredicatesAnd();
    }
}
