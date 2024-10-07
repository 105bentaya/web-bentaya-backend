package org.scouts105bentaya.shared.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SpecificationPredicateHelper {

    private final List<Predicate> predicates;
    private final CriteriaBuilder cb;

    public SpecificationPredicateHelper(CriteriaBuilder cb) {
        this.cb = cb;
        this.predicates = new ArrayList<>();
    }

    public void equal(Expression<String> root, Object filter) {
        if (filter != null) {
            predicates.add(cb.equal(root, filter));
        }
    }

    public void like(Expression<String> root, String filter) {
        if (filter != null && !filter.isEmpty()) {
            predicates.add(cb.like(root, "%" + filter + "%"));
        }
    }

    public void inList(Expression<String> root, List<?> filter) {
        if (filter != null && !filter.isEmpty()) {
            predicates.add(root.in(filter));
        }
    }

    public void dateBetweenFilterRange(Expression<ZonedDateTime> root, String filterStartDate, String filterEndDate) {
        if (filterStartDate != null && filterEndDate != null) {
            ZonedDateTime filterRangeStartDate = ZonedDateTime.parse(filterStartDate);
            ZonedDateTime filterRangeEndDate = ZonedDateTime.parse(filterEndDate);

            Predicate p1 = cb.greaterThanOrEqualTo(root, filterRangeStartDate);
            Predicate p2 = cb.lessThanOrEqualTo(root, filterRangeEndDate);

            predicates.add(cb.and(p1, p2));
        }
    }

    public void dateRangeIntersectsFilterDateRange(Expression<ZonedDateTime> rootStart, Expression<ZonedDateTime> rootEnd, String filterStartDate, String filterEndDate) {
        if (filterStartDate != null && filterEndDate != null) {
            ZonedDateTime filterFirstDate = ZonedDateTime.parse(filterStartDate);
            ZonedDateTime filterSecondDate = ZonedDateTime.parse(filterEndDate);

            Predicate p1 = cb.greaterThanOrEqualTo(rootStart, filterFirstDate);
            Predicate p2 = cb.lessThanOrEqualTo(rootStart, filterSecondDate);

            Predicate p3 = cb.greaterThanOrEqualTo(rootEnd, filterFirstDate);
            Predicate p4 = cb.lessThanOrEqualTo(rootEnd, filterSecondDate);

            Predicate p5 = cb.greaterThanOrEqualTo(rootEnd, filterSecondDate);
            Predicate p6 = cb.lessThanOrEqualTo(rootStart, filterFirstDate);

            predicates.add(cb.or(cb.and(p1, p2), cb.and(p3, p4), cb.and(p5, p6)));
        }
    }

    public void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    public Predicate getPredicatesAnd() {
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

    public Predicate getPredicatesOr() { //todo not working >:(
        return (cb.or(predicates.toArray(new Predicate[]{})));
    }
}