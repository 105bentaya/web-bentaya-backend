package org.scouts105bentaya.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.constraint.IsUnit;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.enums.Roles;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotEmpty(message = "The user must have a role assigned")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    private boolean enabled = true;
    //todo rename to Group
    @IsUnit
    @Enumerated(EnumType.ORDINAL)
    private Group groupId;
    @ManyToMany
    @JoinTable(
            name = "scout_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "scout_id")
    )
    private Set<Scout> scoutList;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Booking> bookingList;

    @Transient
    public boolean hasRole(Roles roleEnum) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleEnum.name()));
    }

    @Transient
    public boolean isMember() {
        return hasRole(Roles.ROLE_USER) || hasRole(Roles.ROLE_SCOUTER) || hasRole(Roles.ROLE_GROUP_SCOUTER);
    }
}
