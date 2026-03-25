package com.example.ecommerceproject.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.ecommerceproject.dto.AdminCategoryResponseDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataValueRequestDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.MetadataFieldResponseDTO;
import com.example.ecommerceproject.dto.ProductResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;

public interface AdminService{
    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, String email);
    Page<SellerResponseDTO> getAllSellers(int page, int size, String sort, String email);
    ApiResponseDTO activateUser(Long userId);
    ApiResponseDTO deactivateUser(Long userId);
    ApiResponse addMetadataField(String fieldName);
    Page<MetadataFieldResponseDTO> getAllMetadataFields(String query, int max, int offset, String sort, String order);
    ApiResponse addCategory(String categoryName, Long parentId);
    Page<AdminCategoryResponseDTO> getAllCategories(String query, Long categoryId, int max, int offset, String sort, String order);
    ApiResponse updateCategory(Long categoryId, String categoryName);
    ApiResponse addCategoryMetadataFieldValues(Long cateoryId, List<CategoryMetadataValueRequestDTO> fieldValues);
    Page<ProductResponseDTO> getAllProducts(Map<String, String> params);
    ApiResponse toggleProductStatus(Long productId, boolean activate);
}
