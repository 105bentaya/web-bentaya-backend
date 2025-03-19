package org.scouts105bentaya.features.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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
    @ManyToOne
    private ScoutCenterFile rulePdf;
    @ManyToOne
    private ScoutCenterFile incidencesDoc;
    @ManyToOne
    private ScoutCenterFile attendanceDoc;
    @ManyToMany
    private List<ScoutCenterFile> photos;
    @Column(nullable = false)
    private String information;
    @ElementCollection
    @Column(nullable = false)
    private List<String> features;
    @Column(nullable = false)
    private int price;
}
