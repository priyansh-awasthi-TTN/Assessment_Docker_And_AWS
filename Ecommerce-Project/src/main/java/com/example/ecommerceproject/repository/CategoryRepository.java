package com.example.ecommerceproject.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category>{
    boolean existsByNameIgnoreCase(String name);
    @Query("select c from Category c where SIZE(c.subCategories) = 0")
    List<Category> findAllLeafNodes();
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategoryId(Long parentId);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :id AND c.subCategories IS EMPTY")
    boolean isLeafNode(@Param("id") Long id);
    Page<Category> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
