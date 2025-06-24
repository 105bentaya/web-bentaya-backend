package org.scouts105bentaya.features.shop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    private ProductImage image;
    @Column(nullable = false, length = 1023)
    private String description;
    @Column(nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProductSize> stockList;
}
