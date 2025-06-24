package org.scouts105bentaya.features.shop.repository;

import org.scouts105bentaya.features.shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    Optional<ProductImage> findByUuid(String uuid);
}
