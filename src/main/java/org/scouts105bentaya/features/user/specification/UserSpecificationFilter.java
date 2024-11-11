package org.scouts105bentaya.features.user.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class UserSpecificationFilter extends SpecificationFilter {
    private String description;
    private boolean showHidden;
    private List<String> roleIds;
}
