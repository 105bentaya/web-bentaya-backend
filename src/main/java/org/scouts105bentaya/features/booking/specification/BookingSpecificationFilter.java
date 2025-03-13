package org.scouts105bentaya.features.booking.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.features.booking.ScoutCenter;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class BookingSpecificationFilter extends SpecificationFilter {
    private List<ScoutCenter> scoutCenters;
    private List<BookingStatus> statuses;
    private String organizationName;
    private String cif;
    private String[] filterDates;
}
