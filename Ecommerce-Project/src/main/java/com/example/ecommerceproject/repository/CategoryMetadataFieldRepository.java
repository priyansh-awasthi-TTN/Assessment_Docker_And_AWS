package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.CategoryMetadataField;

@Repository
public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long>, JpaSpecificationExecutor<CategoryMetadataField>{
    boolean existsByNameIgnoreCase(String name);
}
