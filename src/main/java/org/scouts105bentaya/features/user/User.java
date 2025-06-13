package org.scouts105bentaya.features.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.user.role.Role;
import org.scouts105bentaya.features.user.role.RoleEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Accessors(chain = true)
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
    private boolean enabled = true;

    @NotEmpty(message = "The user must have a role assigned")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToOne
    @JoinTable(
        name = "user_scouter",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "scout_id")
    )
    private Scout scouter;

    @ManyToMany
    @JoinTable(
        name = "user_scouts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "scout_id")
    )
    private Set<Scout> scoutList = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<GeneralBooking> bookingList;

    @Transient
    public boolean hasRole(RoleEnum roleEnum) {
        return roles.stream().anyMatch(role -> role.getName() == roleEnum);
    }

    @Transient
    public boolean isMember() {
        return hasRole(RoleEnum.ROLE_USER) || hasRole(RoleEnum.ROLE_SCOUTER);
    }
}
