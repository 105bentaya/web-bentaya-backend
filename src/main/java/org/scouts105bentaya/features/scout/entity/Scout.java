package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.user.User;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Scout extends Member {

    @Enumerated(EnumType.STRING)
    private ScoutType scoutType;

    @OneToMany(mappedBy = "scout")
    private List<ScoutRegistrationDates> registrationDates;

    private boolean active;

    private boolean federated;

    private Integer census;

    private boolean imageAuthorization;

    @OneToOne(mappedBy = "scout", optional = false)
    private MedicalData medicalData;

    @OneToMany(mappedBy = "scout", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ScoutContact> contactList;

    @OneToMany(mappedBy = "scout", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;

    @ManyToMany(mappedBy = "scoutList", fetch = FetchType.LAZY)
    private Set<User> userList;

    @Column(columnDefinition = "text")
    private String progressionsOld;

    @Column(columnDefinition = "text")
    private String observationsOld;

    @ManyToOne
    private Group group;

    @OneToOne
    private MemberFile photo;

    @Override
    public RealPersonalData getPersonalData() {
        return (RealPersonalData) super.getPersonalData();
    }
}
