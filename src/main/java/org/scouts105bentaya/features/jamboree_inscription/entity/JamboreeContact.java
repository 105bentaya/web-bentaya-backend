package org.scouts105bentaya.features.jamboree_inscription.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class JamboreeContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String surname;
    private String name;
    private String mobilePhone;
    private String landlinePhone;
    private String email;

    @ManyToOne(optional = false)
    private JamboreeInscription inscription;
}
