package com.example.ecommerceproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
    @Query("select distinct p.brand from Product p where p.category.id IN :categoryIds AND p.brand IS NOT NULL")
    List<String> findDistinctBrandsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("select MIN(pv.price) from ProductVariations pv where pv.product.category.id IN :categoryIds AND pv.isActive = true")
    Double findMinPriceByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT MAX(pv.price) FROM ProductVariations pv WHERE pv.product.category.id IN :categoryIds AND pv.isActive = true")
    Double findMaxPriceByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    boolean existsByCategory_IdAndIsDeletedFalse(Long categoryId);

    boolean existsByNameAndBrandAndCategory_IdAndSeller_Id(
        String name, String brand, Long categoryId, Long sellerId
    );

    Optional<Product> findByIdAndSeller_IdAndIsDeletedFalse(Long id, Long sellerId);

    Page<Product> findAllBySeller_IdAndIsDeletedFalse(Long id, Pageable pageable);

    Optional<Product> findByIdAndIsDeletedFalseAndIsActiveTrue(Long productId);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds " +
           "AND p.isActive = true AND p.isDeleted = false " +
           "AND EXISTS (SELECT v FROM ProductVariations v WHERE v.product = p AND v.isActive = true)")
    Page<Product> findActiveProductsWithActiveVariationsByCategoryIds(
            @Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :excludeProductId " +
           "AND p.isActive = true AND p.isDeleted = false " +
           "AND EXISTS (SELECT v FROM ProductVariations v WHERE v.product = p AND v.isActive = true)")
    Page<Product> findSimilarProducts(
            @Param("categoryId") Long categoryId,
            @Param("excludeProductId") Long excludeProductId,
            Pageable pageable);

    boolean existsByIdAndSeller_User_Id(Long productId, Long userId);
}
