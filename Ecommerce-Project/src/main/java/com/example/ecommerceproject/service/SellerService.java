package com.example.ecommerceproject.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.ProductCreateRequest;
import com.example.ecommerceproject.dto.ProductResponseDTO;
import com.example.ecommerceproject.dto.ProductUpdateRequest;
import com.example.ecommerceproject.dto.ProductVariationResponse;
import com.example.ecommerceproject.dto.SellerCategoryResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;

public interface SellerService {
    SellerProfileResponseDTO getProfile(Long userId);
    ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto);
    ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto);
    ApiResponseDTO updateAddress(Long userId, AddressPartialUpdateRequestDTO dto);
    List<SellerCategoryResponseDTO> getAllLeafCategories();
    ApiResponse createProduct(ProductCreateRequest dto);
    ApiResponse createProductVariation(Long productId, String variationJson, MultipartFile primaryImage, MultipartFile[] secondaryImages);
    ApiResponse updateProductVariation(Long productId, Long variationId, String variationJson, MultipartFile primaryImage, MultipartFile[] secondaryImages);
    Page<ProductResponseDTO> getAllProducts(Map<String, String> params);
    Page<ProductVariationResponse> getProductVariations(Long productId, int offset, int max, String sort, String order);
    ApiResponse deleteProduct(Long productId);
    ApiResponse updateProduct(Long productId, ProductUpdateRequest dto);
}
