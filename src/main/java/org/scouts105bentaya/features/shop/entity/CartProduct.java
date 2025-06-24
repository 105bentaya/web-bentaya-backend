package org.scouts105bentaya.features.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.user.User;

@Entity
@Getter
@Setter
@IdClass(CartProductId.class)
public class CartProduct {
    @Id
    @ManyToOne(optional = false)
    private User user;
    @Id
    @ManyToOne(optional = false)
    private ProductSize productSize;
    private int count;
}
