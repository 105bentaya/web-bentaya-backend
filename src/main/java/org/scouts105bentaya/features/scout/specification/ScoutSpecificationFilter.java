package org.scouts105bentaya.features.scout.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.features.group.Section;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScoutSpecificationFilter extends SpecificationFilter {
    private String name;
    private String census;
    private String[] filterDates;
    private List<String> genders;
    private String idDocument;
    private String email;
    private Boolean imageAuthorization;
    private List<ScoutStatus> statuses;
    private List<ScoutType> scoutTypes;

    private List<Integer> groupIds;
    private List<ScoutType> groupScoutTypes;
    private List<Section> sections;
}
