package com.example.ecommerceproject.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.AddressResponseDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryFilterDetailsDTO;
import com.example.ecommerceproject.dto.CategoryResponseDTO;
import com.example.ecommerceproject.dto.CustomerProductListResponseDTO;
import com.example.ecommerceproject.dto.CustomerProductViewResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileUpdateRequestDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customer Profile Management", description = "APIs for Customers to manage their profile and multiple addresses")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    final CustomerService customerService;

    @Operation(summary = "View Customer Profile")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<CustomerProfileResponseDTO> getProfile(
        @PathVariable
        @Positive(message = "{validation.invalid_id_format}")
        Long userId
    ){
        return ResponseEntity.ok(customerService.getProfile(userId));
    }

    @Operation(summary = "Update Profile", description = "Partial update of customer profile fields")
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseDTO> updateProfile(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            @Valid @RequestBody CustomerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateProfile(userId, dto));
    }

    @Operation(summary = "Update Password")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponseDTO> updatePassword(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updatePassword(userId, dto));
    }

    @Operation(summary = "View All Addresses")
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId) {
        return ResponseEntity.ok(customerService.getAddresses(userId));
    }

    @Operation(summary = "Add New Address")
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<ApiResponseDTO> addAddress(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(customerService.addAddress(userId, dto));
    }

    @Operation(summary = "Update Address", description = "Partial update of a specific address")
    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> updateAddress(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long addressId,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateAddress(userId, addressId, dto));
    }

    @Operation(summary = "Delete Address", description = "Soft deletes a specific address")
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> deleteAddress(
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long addressId) {
        return ResponseEntity.ok(customerService.deleteAddress(userId, addressId));
    }

    @Operation(summary = "List categories", description = "Returns root categories if no ID is passed, else returns immediate child nodes.")
    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponseDTO>> listCategories(
            @Parameter(description = "Optional parent Category ID")
            @RequestParam(required = false)
            @Positive(message = "{validation.invalid_id_format}")
            Long categoryId) {
        return ResponseEntity.ok(customerService.getCategories(categoryId));
    }

    @Operation(summary = "Fetch filtering details for a category", description = "Returns metadata, aggregated brands, and price ranges for a category and all its subcategories.")
    @GetMapping("/category/{categoryId}/filters")
    public ResponseEntity<CategoryFilterDetailsDTO> getFilteringDetails(
            @Parameter(description = "Valid Category ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long categoryId) {
        return ResponseEntity.ok(customerService.getCategoryFilteringDetails(categoryId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "View a single product", description = "Retrieves detailed information of an active product including all variations and images.")
    public ResponseEntity<CustomerProductViewResponseDTO> getProductDetails(
            @Parameter(description = "ID of the product to view") @PathVariable Long productId) {
        return ResponseEntity.ok(customerService.getProductDetails(productId));
    }

    @GetMapping("/category/{categoryId}/products")
    @Operation(summary = "View products by category", description = "Retrieves a paginated list of active products for a category (and its subcategories).")
    public ResponseEntity<Page<CustomerProductListResponseDTO>> getProductsByCategory(
            @Parameter(description = "ID of the category") @PathVariable Long categoryId,
            @Parameter(description = "Max records per page") @RequestParam(defaultValue = "10") int max,
            @Parameter(description = "Page offset (0-indexed)") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort order (ASC/DESC)") @RequestParam(defaultValue = "ASC") String order) {
        return ResponseEntity.ok(customerService.getAllProductsByCategory(categoryId, offset, max, sort, order));
    }

    @GetMapping("/{productId}/similar")
    @Operation(summary = "View similar products", description = "Retrieves a paginated list of active products belonging to the same category as the given product.")
    public ResponseEntity<Page<CustomerProductListResponseDTO>> getSimilarProducts(
            @Parameter(description = "ID of the base product") @PathVariable Long productId,
            @Parameter(description = "Max records per page") @RequestParam(defaultValue = "10") int max,
            @Parameter(description = "Page offset (0-indexed)") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort order (ASC/DESC)") @RequestParam(defaultValue = "ASC") String order) {
        return ResponseEntity.ok(customerService.getSimilarProducts(productId, offset, max, sort, order));
    }
}
