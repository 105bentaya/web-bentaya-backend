package org.scouts105bentaya.features.scout_contact;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.Scout;

@Getter
@Setter
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String relationship;
    private String phone;
    private String email;
    @ManyToOne
    @JoinColumn(name = "scout_id")
    private Scout scout;
}
