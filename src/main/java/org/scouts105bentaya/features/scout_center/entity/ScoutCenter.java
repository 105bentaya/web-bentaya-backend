package org.scouts105bentaya.features.scout_center.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.booking.entity.BookingDocumentType;

import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class ScoutCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 127)
    private String name;
    @Column(nullable = false, length = 127)
    private String place;
    @Column(nullable = false)
    private int maxCapacity;
    @Column(nullable = false)
    private int minExclusiveCapacity;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ScoutCenterFile rulePdf;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ScoutCenterFile incidencesDoc;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ScoutCenterFile attendanceDoc;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ScoutCenterFile mainPhoto;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScoutCenterFile> photos;
    @Column(nullable = false, length = 1023)
    private String information;
    @ElementCollection
    @Column(nullable = false)
    private List<String> features;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false, length = 63)
    private String icon;
    @Column(nullable = false, length = 7)
    private String color;

    @Transient
    public boolean isAlwaysExclusive() {
        return this.minExclusiveCapacity < 1;
    }
}
