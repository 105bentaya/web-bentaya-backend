package org.scouts105bentaya.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.specification.util.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements Specification<User> {

    private final UserFilterDto userFilterDto;

    public UserSpecification(UserFilterDto userFilterDto) {
        this.userFilterDto = userFilterDto;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        predicates.like(root.get("username"), userFilterDto.getDescription());
        predicates.inList(root.get("roles").get("id"), userFilterDto.getRoleIds());

        if (!userFilterDto.isShowHidden()) {
            predicates.addPredicate(criteriaBuilder.isTrue(root.get("enabled")));
        }

        return predicates.getPredicatesAnd();
    }
}
