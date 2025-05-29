package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Scout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoutType scoutType;

    @ManyToOne
    private Group group;

    @OneToMany(mappedBy = "scout", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ScoutRegistrationDates> registrationDates;

    private boolean active;

    private boolean federated;

    private Integer census;

    private boolean imageAuthorization;

    @OneToOne(mappedBy = "scout", optional = false)
    private PersonalData personalData;

    @OneToOne(mappedBy = "scout", optional = false)
    private MedicalData medicalData;

    @OneToMany(mappedBy = "scout", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ScoutContact> contactList;

    @OneToMany(mappedBy = "scout", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;

    @OneToMany(mappedBy = "scout", cascade = CascadeType.MERGE)
    private List<ScoutRecord> recordList;

    @ManyToMany(mappedBy = "scoutList", fetch = FetchType.LAZY)
    private Set<User> userList;

    @Column(columnDefinition = "text")
    private String progressionsOld;

    @Column(columnDefinition = "text")
    private String observationsOld;

    @OneToMany(mappedBy = "scout")
    private List<SpecialMember> specialRoles;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany
    private List<ScoutFile> extraFiles;

    @OneToMany
    private List<ScoutFile> images;
}
