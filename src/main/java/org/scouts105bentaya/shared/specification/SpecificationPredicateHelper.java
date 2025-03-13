package org.scouts105bentaya.shared.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
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

    public void same(Expression<String> root, Object filter) {
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

    public void localDateTimeBetweenFilterRange(Expression<LocalDateTime> root, String filterStartDate, String filterEndDate) {
        if (filterStartDate != null && filterEndDate != null) {
            dateBetweenFilterRange(root, LocalDateTime.parse(filterStartDate), LocalDateTime.parse(filterEndDate));
        }
    }

    public void zonedDateTimeBetweenFilterRange(Expression<ZonedDateTime> root, String filterStartDate, String filterEndDate) {
        if (filterStartDate != null && filterEndDate != null) {
            dateBetweenFilterRange(root, ZonedDateTime.parse(filterStartDate), ZonedDateTime.parse(filterEndDate));
        }
    }

    private <T extends Comparable<T>> void dateBetweenFilterRange(Expression<? extends T> root, T filterStartDate, T filterEndDate) {
        Predicate p1 = cb.greaterThanOrEqualTo(root, filterStartDate);
        Predicate p2 = cb.lessThanOrEqualTo(root, filterEndDate);

        predicates.add(cb.and(p1, p2));
    }


    public void zonedDateTimeRangeIntersectsFilterDateRange(Expression<ZonedDateTime> rootStart, Expression<ZonedDateTime> rootEnd, String filterStartDate, String filterEndDate) {
        if (filterStartDate != null && filterEndDate != null) {
            dateRangeIntersectsDateRange(rootStart, rootEnd, ZonedDateTime.parse(filterStartDate), ZonedDateTime.parse(filterEndDate));
        }
    }

    public void localDateTimeRangeIntersectsFilterDateRange(Expression<LocalDateTime> rootStart, Expression<LocalDateTime> rootEnd, String[] filterDates) {
        if (filterDates != null && filterDates.length == 2) {
            dateRangeIntersectsDateRange(rootStart, rootEnd, LocalDateTime.parse(filterDates[0]), LocalDateTime.parse(filterDates[1]));
        }
    }

    private <T extends Comparable<T>> void dateRangeIntersectsDateRange(Expression<? extends T> rootStart, Expression<? extends T> rootEnd, T filterFirstDate, T filterSecondDate) {
        Predicate p1 = cb.greaterThanOrEqualTo(rootStart, filterFirstDate);
        Predicate p2 = cb.lessThanOrEqualTo(rootStart, filterSecondDate);

        Predicate p3 = cb.greaterThanOrEqualTo(rootEnd, filterFirstDate);
        Predicate p4 = cb.lessThanOrEqualTo(rootEnd, filterSecondDate);

        Predicate p5 = cb.greaterThanOrEqualTo(rootEnd, filterSecondDate);
        Predicate p6 = cb.lessThanOrEqualTo(rootStart, filterFirstDate);

        predicates.add(cb.or(cb.and(p1, p2), cb.and(p3, p4), cb.and(p5, p6)));
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