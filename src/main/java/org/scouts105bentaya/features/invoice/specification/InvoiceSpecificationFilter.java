package org.scouts105bentaya.features.invoice.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class InvoiceSpecificationFilter extends SpecificationFilter {
    private String issuer;
    private List<Integer> expenseTypes;
    private List<Integer> grants;
    private List<Integer> payers;
    private String[] filterDates;
}
