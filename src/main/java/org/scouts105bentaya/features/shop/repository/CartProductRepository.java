package org.scouts105bentaya.features.shop.repository;

import org.scouts105bentaya.features.shop.entity.CartProduct;
import org.scouts105bentaya.features.shop.entity.CartProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
}
