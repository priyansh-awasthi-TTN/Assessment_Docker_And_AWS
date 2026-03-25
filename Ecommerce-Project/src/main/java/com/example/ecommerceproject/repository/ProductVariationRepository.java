package com.example.ecommerceproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceproject.entity.ProductVariations;

public interface ProductVariationRepository extends JpaRepository<ProductVariations, Long>{
    Page<ProductVariations> findAllByProductId(Long productId, Pageable pageable);
    List<ProductVariations> findAllByProduct_IdAndProduct_IsActiveTrueAndProduct_IsDeletedFalse(Long productId);
    Optional<ProductVariations> findByIdAndProductId(Long variationId, Long productId);
    long countByProduct_IdAndProduct_IsDeletedFalse(Long productId);
    Optional<ProductVariations> findByIdAndProduct_IdAndProduct_IsDeletedFalse(Long variationId, Long productId);
}
