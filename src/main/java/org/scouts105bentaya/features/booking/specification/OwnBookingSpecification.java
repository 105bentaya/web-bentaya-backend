package org.scouts105bentaya.features.booking.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;

public class OwnBookingSpecification implements Specification<OwnBooking> {

    private final BookingSpecificationFilter filter;

    public OwnBookingSpecification(BookingSpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<OwnBooking> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        if (filter.getGroupIds() != null) {
            SpecificationPredicateHelper groupPredicate = new SpecificationPredicateHelper(criteriaBuilder);
            if (filter.getGroupIds().contains(0)) {
                groupPredicate.addPredicate(criteriaBuilder.isNull(root.get("group")));
            }
            groupPredicate.inList(root.get("group").get("id"), filter.getGroupIds());
            predicates.addPredicate(groupPredicate.getPredicatesOr());
        }

        predicates.inList(root.get("scoutCenter").get("id"), filter.getScoutCenters());
        predicates.inList(root.get("status"), filter.getStatuses());
        predicates.localDateTimeIsAfterDate(root.get("endDate"), filter.getEndDate());
        predicates.localDateTimeRangeIntersectsFilterDateRange(root.get("startDate"), root.get("endDate"), filter.getFilterDates());

        return predicates.getPredicatesAnd();
    }
}
