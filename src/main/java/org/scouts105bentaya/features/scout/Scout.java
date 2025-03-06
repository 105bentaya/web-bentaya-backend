package org.scouts105bentaya.features.scout;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.user.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Scout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @ManyToOne
    private Group group;
    private String name;
    private String surname;
    private String dni;
    private String medicalData;
    private String gender;
    private Date birthday;
    private boolean imageAuthorization;
    private String shirtSize;
    private String municipality;
    private Integer census;
    @Column(columnDefinition = "TEXT")
    private String progressions;
    @Column(columnDefinition = "TEXT")
    private String observations;
    @OneToMany(mappedBy = "scout", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<Contact> contactList;
    @OneToMany(mappedBy = "scout", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;
    @ManyToMany(mappedBy = "scoutList", fetch = FetchType.LAZY)
    private Set<User> userList;
    private boolean enabled;
}