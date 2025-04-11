package org.scouts105bentaya.features.invoice.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.scouts105bentaya.shared.specification.SpecificationPredicateHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class InvoiceSpecification implements Specification<Invoice> {

    private final InvoiceSpecificationFilter filter;

    public InvoiceSpecification(InvoiceSpecificationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Invoice> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        SpecificationPredicateHelper predicates = new SpecificationPredicateHelper(criteriaBuilder);

        if (filter.getIssuer() != null && !filter.getIssuer().isBlank()) {
            SpecificationPredicateHelper namePredicates = new SpecificationPredicateHelper(criteriaBuilder);
            namePredicates.like(root.get("nif"), filter.getIssuer());
            namePredicates.like(root.get("issuer"), filter.getIssuer());
            predicates.addPredicate(namePredicates.getPredicatesOr());
        }

        predicates.inList(root.get("expenseType").get("id"), filter.getExpenseTypes());
        predicates.inList(root.get("grant").get("id"), filter.getGrants());
        predicates.inList(root.get("payer").get("id"), filter.getPayers());
        predicates.localDateBetweenFilterRange(root.get("invoiceDate"), filter.getFilterDates());

        return predicates.getPredicatesAnd();
    }
}
