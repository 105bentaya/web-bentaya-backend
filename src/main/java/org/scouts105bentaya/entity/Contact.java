package org.scouts105bentaya.entity;

import jakarta.persistence.*;

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

    public Contact() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Scout getScout() {
        return scout;
    }

    public void setScout(Scout scout) {
        this.scout = scout;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", relationship='" + relationship + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", scout=" + scout +
                '}';
    }
}
