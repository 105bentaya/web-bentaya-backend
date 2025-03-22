package org.scouts105bentaya.features.scout_center.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class ScoutCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
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
    @Column(nullable = false)
    private String information;
    @ElementCollection
    @Column(nullable = false)
    private List<String> features;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String icon;
}
