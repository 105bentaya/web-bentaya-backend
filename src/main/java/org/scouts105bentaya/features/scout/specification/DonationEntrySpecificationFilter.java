package org.scouts105bentaya.features.scout.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class DonationEntrySpecificationFilter extends SpecificationFilter {
    private String name;
    private String census;
    private String idDocument;
    private String[] issueDates;
    private String[] dueDates;
    private List<Integer> incomeTypes;
    private String description;
}
