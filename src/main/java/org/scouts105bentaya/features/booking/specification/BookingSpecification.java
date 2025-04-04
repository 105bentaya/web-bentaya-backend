package org.scouts105bentaya.features.booking.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;

public class BookingSpecification implements Specification<Booking> {

    private final BookingSpecificationFilter filter;

    public BookingSpecification(BookingSpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        predicates.isEqual(root.get("user").get("id"), filter.getUserId());
        predicates.inList(root.get("scoutCenter").get("id"), filter.getScoutCenters());
        predicates.inList(root.get("status"), filter.getStatuses());
        predicates.like(root.get("organizationName"), filter.getOrganizationName());
        predicates.like(root.get("cif"), filter.getCif());
        predicates.localDateTimeIsAfterDate(root.get("endDate"), filter.getEndDate());
        predicates.localDateTimeRangeIntersectsFilterDateRange(root.get("startDate"), root.get("endDate"), filter.getFilterDates());

        return predicates.getPredicatesAnd();
    }
}
