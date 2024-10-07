package org.scouts105bentaya.features.user.specification;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
public class UserSpecificationFilter extends SpecificationFilter {
    private String description;
    private boolean showHidden;
    private List<String> roleIds;
}
