package org.scouts105bentaya.features.shop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.payment.Payment;
import org.scouts105bentaya.features.user.User;

import java.util.List;

@Entity
@Getter
@Setter
public class ShopPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String phone;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany(mappedBy = "shopPurchase", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BoughtProduct> boughtProducts;

    @OneToOne
    @JoinTable(
        name = "shop_purchase_payment",
        joinColumns = @JoinColumn(name = "shop_purchase_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "payment_id", referencedColumnName = "id")
    )
    private Payment payment;

    @ManyToOne(optional = false)
    private User user;
}
