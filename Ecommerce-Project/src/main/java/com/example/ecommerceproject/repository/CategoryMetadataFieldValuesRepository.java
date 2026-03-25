package com.example.ecommerceproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.CategoryMetadataFieldValues;

@Repository
public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, Long>{
    boolean existsByCategoryIdAndMetadataFieldIdAndValue(Long categoryId, Long metadataFieldId, String value);
    @Query("SELECT cmfv FROM CategoryMetadataFieldValues cmfv JOIN FETCH cmfv.metadataField WHERE cmfv.category.id = :categoryId")
    List<CategoryMetadataFieldValues> findAllByCategory_Id(@Param("categoryId") Long categoryId);
}
