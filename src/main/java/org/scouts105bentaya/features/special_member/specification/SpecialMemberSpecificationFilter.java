package org.scouts105bentaya.features.special_member.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scouts105bentaya.features.special_member.enums.SpecialMemberRole;
import org.scouts105bentaya.shared.specification.SpecificationFilter;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SpecialMemberSpecificationFilter extends SpecificationFilter {
    private String census;
    private String name;
    private String doi;
    private List<SpecialMemberRole> roles;
}
