package org.scouts105bentaya.specification;

import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.specification.util.SpecificationFilter;

import java.util.List;

@Getter
@Setter
public class UserFilterDto extends SpecificationFilter {
    private String description;
    private boolean showHidden;
    private List<String> roleIds;
}
