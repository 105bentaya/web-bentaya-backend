package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.enums.MemberType;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type;

    @OneToMany(mappedBy = "member")
    private Set<MemberRoleInfo> roles;

    @OneToOne(mappedBy = "member", optional = false)
    private PersonalData personalData;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany
    private List<MemberFile> extraFiles;

    @OneToMany
    private List<MemberFile> images;
}
