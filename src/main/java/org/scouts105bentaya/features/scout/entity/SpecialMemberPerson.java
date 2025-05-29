package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.scout.enums.PersonType;

import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class SpecialMemberPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonType type;
    private String name;
    private String surname;
    private String companyName;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    private IdentificationDocument idDocument;
    private String phone;
    private String email;

    @OneToMany(mappedBy = "person")
    private List<SpecialMember> specialMembers;
}
