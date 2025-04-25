package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PersonalData {
    @Id
    @JsonIgnore
    private Integer memberId;

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @Nullable
    private IdentificationDocument idDocument;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<MemberFile> documents;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;
}
