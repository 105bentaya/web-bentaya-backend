package org.scouts105bentaya.features.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.donation.Donation;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderNumber;
    @NotNull
    private Integer amount;
    @NotNull
    private Integer status;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private PaymentTypeEnum paymentType;
    private ZonedDateTime modificationDate;
    @OneToOne(mappedBy = "payment")
    private Donation donation;
    @OneToOne(mappedBy = "payment")
    private ShopPurchase shopPurchase;
}
