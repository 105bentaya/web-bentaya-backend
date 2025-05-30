package org.scouts105bentaya.features.special_member.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.features.special_member.SpecialMemberRole;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SpecialMemberSpecificationFilter extends SpecificationFilter {
    private String census;
    private String name;
    private List<SpecialMemberRole> roles;
}
