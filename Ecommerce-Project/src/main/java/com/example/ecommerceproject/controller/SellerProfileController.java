package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.dto.*;
import com.example.ecommerceproject.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Seller Profile Management", description = "APIs for Sellers to manage their profile, password, and addresses")
@SecurityRequirement(name = "bearerAuth")
public class SellerProfileController {

    private final SellerService sellerService;

    @Operation(summary = "View Seller Profile", description = "Retrieves the complete profile data of the currently logged-in seller.")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<SellerProfileResponseDTO> getProfile(
            @PathVariable @Positive(message = "{validation.invalid_id_format}") Long userId) {
        return ResponseEntity.ok(sellerService.getProfile(userId));
    }

    @Operation(summary = "Update Seller Profile", description = "Updates basic profile information (first name, last name, company details) for the logged-in seller.")
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseDTO> updateProfile(
            @PathVariable @Positive(message = "{validation.invalid_id_format}") Long userId,
            @Valid @RequestBody SellerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateProfile(userId, dto));
    }

    @Operation(summary = "Update Password", description = "Updates the password for the logged-in seller. Enforces password complexity rules.")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponseDTO> updatePassword(
            @PathVariable @Positive(message = "{validation.invalid_id_format}") Long userId,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updatePassword(userId, dto));
    }

    @Operation(summary = "Update Address", description = "Updates the seller's address (sellers can only have one address).")
    @PatchMapping("/{userId}/address")
    public ResponseEntity<ApiResponseDTO> updateAddress(
            @PathVariable @Positive(message = "{validation.invalid_id_format}") Long userId,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateAddress(userId, dto));
    }

    @Operation(summary = "List all leaf categories", description = "Fetches all leaf node categories along with their parent chain details and metadata fields.")
    @GetMapping("/category/all")
    public ResponseEntity<List<SellerCategoryResponseDTO>> listAllCategories() {
        return ResponseEntity.ok(sellerService.getAllLeafCategories());
    }

    @PostMapping("/products")
    @Operation(summary = "Add a product", description = "Creates a new product (defaults to inactive state).")
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(sellerService.createProduct(request));
    }

    @PostMapping(value = "/product/{productId}/variations", consumes = "multipart/form-data")
    @Operation(summary = "Add a product variation with images", description = "Adds a variation with metadata, pricing, and images to an active product.")
    public ResponseEntity<ApiResponse> addProductVariation(
            @Parameter(description = "ID of the parent product") @PathVariable Long productId,
            @Parameter(description = "Variation data as JSON string") @RequestParam("variation") String variationJson,
            @Parameter(description = "Primary image file") @RequestParam("primaryImage") MultipartFile primaryImage,
            @Parameter(description = "Secondary image files") @RequestParam(value = "secondaryImages", required = false) MultipartFile[] secondaryImages) {
        return ResponseEntity.ok(sellerService.createProductVariation(productId, variationJson, primaryImage, secondaryImages));
    }

    @GetMapping("/products")
    @Operation(summary = "List all products", description = "Retrieve a paginated list of all products with optional filters")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Map containing offset, max, sort, order, sellerId, or categoryId")
            @RequestParam Map<String, String> params) {
        return ResponseEntity.ok(sellerService.getAllProducts(params));
    }

    @GetMapping("/{productId}/variations")
    @Operation(summary = "View product variations", description = "Retrieves paginated variations for a specific product.")
    public ResponseEntity<Page<ProductVariationResponse>> viewProductVariations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String order) {
        return ResponseEntity.ok(sellerService.getProductVariations(productId, offset, max, sort, order));
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Delete a product", description = "Soft deletes a specific product.")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(sellerService.deleteProduct(productId));
    }

    @PutMapping("/product/{productId}")
    @Operation(summary = "Update a product", description = "Updates optional fields of an existing product.")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(sellerService.updateProduct(productId, request));
    }

    @PutMapping(value = "/product/{productId}/variations/{variationId}", consumes = "multipart/form-data")
    @Operation(summary = "Update a variation with images", description = "Updates variation details and images.")
    public ResponseEntity<ApiResponse> updateProductVariation(
            @PathVariable Long productId,
            @PathVariable Long variationId,
            @Parameter(description = "Variation data as JSON string") @RequestParam("variation") String variationJson,
            @Parameter(description = "Primary image file") @RequestParam(value = "primaryImage", required = false) MultipartFile primaryImage,
            @Parameter(description = "Secondary image files") @RequestParam(value = "secondaryImages", required = false) MultipartFile[] secondaryImages) {
        return ResponseEntity.ok(sellerService.updateProductVariation(productId, variationId, variationJson, primaryImage, secondaryImages));
    }
}
