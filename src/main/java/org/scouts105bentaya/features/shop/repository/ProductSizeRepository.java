package org.scouts105bentaya.features.shop.repository;

import org.scouts105bentaya.features.shop.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
}
