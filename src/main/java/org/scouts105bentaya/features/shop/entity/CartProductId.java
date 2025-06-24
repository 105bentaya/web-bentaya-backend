package org.scouts105bentaya.features.shop.entity;

import java.io.Serializable;
import java.util.Objects;

public class CartProductId implements Serializable {

    private Integer user;

    private Integer productSize;

    public CartProductId(Integer user, Integer productSize) {
        this.user = user;
        this.productSize = productSize;
    }

    public CartProductId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProductId that = (CartProductId) o;
        return Objects.equals(user, that.user) && Objects.equals(productSize, that.productSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, productSize);
    }
}
