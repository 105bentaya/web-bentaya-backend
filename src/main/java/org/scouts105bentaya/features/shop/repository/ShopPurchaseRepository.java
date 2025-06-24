package org.scouts105bentaya.features.shop.repository;

import org.scouts105bentaya.features.shop.entity.ShopPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopPurchaseRepository extends JpaRepository<ShopPurchase, Integer> {
}
