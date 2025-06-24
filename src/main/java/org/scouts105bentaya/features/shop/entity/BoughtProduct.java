package org.scouts105bentaya.features.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoughtProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String sizeName;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private Integer price;
    @ManyToOne(optional = false)
    @JsonIgnore
    private ShopPurchase shopPurchase;
}
