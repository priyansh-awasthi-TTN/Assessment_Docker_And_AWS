package com.example.ecommerceproject.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.AdminCategoryResponseDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataValueRequestDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.MetadataFieldResponseDTO;
import com.example.ecommerceproject.dto.ProductResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;
import com.example.ecommerceproject.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Management", description = "APIs for Administrator to manage customers and sellers")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    final AdminService adminService;

    @Operation(summary = "Get all registered customers", description = "Retrieves a paginated list of all registered customers. Can be sorted and filtered by email.")
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerResponseDTO>> getCustomer(
        @Parameter(description = "Page number to retrieve (starts at 0)")
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "{validation.page_offset_negative}") int page,

        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "{validation.page_size_min}")
        @Max(value = 100, message = "{validation.page_size_max}") int size,

        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(required = false) String email
    ){
        return ResponseEntity.ok(adminService.getAllCustomers(
            page, size, sort, email));
    }

    @Operation(summary = "Get all registered sellers", description = "Retrieves a paginated list of all registered sellers. Can be sorted and filtered by email.")
    @GetMapping("/sellers")
    public ResponseEntity<Page<SellerResponseDTO>> getSellers(
        @Parameter(description = "Page number to retrieve (starts at 0)")
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "{validation.page_offset_negative}") int page,

        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "{validation.page_size_min}")
        @Max(value = 100, message = "{validation.page_size_max}") int size,

        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(required = false) String email
    ){
        return ResponseEntity.ok(adminService.getAllSellers(page, size, sort, email));
    }

    @Operation(summary = "Activate a customer account", description = "Activates a currently deactivated customer account. Sends an email notification to the customer.")
    @PatchMapping("/user/{userId}/activate")
    public ResponseEntity<ApiResponseDTO> activateUser(
        @Parameter(description = "User ID to activate", required = true)
        @PathVariable
        @Positive(message = "{validation.user_id_positive}") Long userId)
    {
        return ResponseEntity.ok(adminService.activateUser(userId));
    }

    @Operation(summary = "Deactivate a customer account", description = "Deactivates a currently active customer account. Sends an email notification to the customer.")
    @PatchMapping("/user/{userId}/deactivate")
    public ResponseEntity<ApiResponseDTO> deactivateUser(
        @Parameter(description = "User ID to deactivate", required = true)
        @PathVariable
        @Positive(message = "{validation.user_id_positive}")
        Long userId
    )
    {
        return ResponseEntity.ok(adminService.deactivateUser(userId));
    }

    @Operation(summary = "Add a Metadata field", description = "Creates a new, uniquely named metadata field for categories.")
    @PostMapping("/category/metadata-fields")
    public ResponseEntity<ApiResponse> addMetadataField(
            @Parameter(description = "Name of the metadata field to create", required = true)
            @RequestParam
            @NotBlank(message = "{category.field_name_required}")
            @Size(max = 40, message = "{category.field_name_invalid}")
            @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]+$", message = "{validation.field_name_invalid_characters}")
            String fieldName) {
        return ResponseEntity.ok(adminService.addMetadataField(fieldName));
    }

    @Operation(summary = "View all Metadata fields", description = "Fetches a paginated list of all metadata fields. Supports filtering by name.")
    @GetMapping("/category/metadata-fields")
    public ResponseEntity<Page<MetadataFieldResponseDTO>> viewAllMetadataFields(
            @Parameter(description = "Maximum number of records to return")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "{validation.page_size_min}")
            @Max(value = 100, message = "{validation.page_size_max}")
            int max,

            @Parameter(description = "Offset for pagination")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "{validation.page_offset_negative}")
            int offset,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "id")
            String sort,

            @Parameter(description = "Sorting order (ASC or DESC)")
            @RequestParam(defaultValue = "ASC")
            String order,

            @Parameter(description = "Optional query string to filter fields by name")
            @RequestParam(required = false)
            String query) {
        return ResponseEntity.ok(adminService.getAllMetadataFields(query, max, offset, sort, order));
    }

    @Operation(summary = "Add a category", description = "Creates a new category. Can optionally be nested under a parent category.")
    @PostMapping("/category")
    public ResponseEntity<ApiResponse> addCategory(
            @Parameter(description = "Name of the category", required = true)
            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]+$", message = "{category.name_invalid}")
            String categoryName,
            @Parameter(description = "Optional ID of the parent category") @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(adminService.addCategory(categoryName, parentId));
    }

    @Operation(summary = "View all categories", description = "Fetches a paginated list of categories. Can be filtered by parent Category ID or name query.")
    @GetMapping("/category/all")
    public ResponseEntity<Page<AdminCategoryResponseDTO>> viewAllCategories(
            @Parameter(description = "Maximum number of records to return")
            @RequestParam(defaultValue = "10")
            int max,

            @Parameter(description = "Offset for pagination")
            @RequestParam(defaultValue = "0")
            int offset,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "id")
            String sort,

            @Parameter(description = "Sorting order (ASC or DESC)")
            @RequestParam(defaultValue = "ASC")
            String order,

            @Parameter(description = "Optional query string to filter categories by name")
            @RequestParam(required = false)
            String query,

            @Parameter(description = "Optional ID to filter categories by their parent")
            @RequestParam(required = false)
            Long categoryId) {
        return ResponseEntity.ok(adminService.getAllCategories(query, categoryId, max, offset, sort, order));
    }

    @Operation(summary = "Update a category", description = "Updates the name of an existing category.")
    @PutMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(
            @Parameter(description = "ID of the category to update", required = true) @PathVariable Long categoryId,
            @Parameter(description = "New name for the category", required = true)
            @RequestParam
            @NotBlank(message = "{category.name_required}")
            @Size(max = 40, message = "{category.name_invalid}")
            String categoryName) {
        return ResponseEntity.ok(adminService.updateCategory(categoryId, categoryName));
    }

    @Operation(summary = "Add metadata field values to a category", description = "Associates multiple metadata fields and their possible values to a specific category.")
    @PostMapping("/category/{categoryId}/metadata-fields")
    public ResponseEntity<ApiResponse> addCategoryMetadata(
            @Parameter(description = "ID of the category", required = true) @PathVariable Long categoryId,
            @RequestBody @Valid List<CategoryMetadataValueRequestDTO> fieldValues) {
        return ResponseEntity.ok(adminService.addCategoryMetadataFieldValues(categoryId, fieldValues));
    }

    @GetMapping("/products")
    @Operation(summary = "List all products", description = "Retrieve a paginated list of all products with optional filters")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Map containing offset, max, sort, order, sellerId, or categoryId")
            @RequestParam Map<String, String> params) {
        return ResponseEntity.ok(adminService.getAllProducts(params));
    }

    @PutMapping("/products/{id}/status")
    @Operation(summary = "Toggle product status", description = "Activate or Deactivate a product by ID")
    public ResponseEntity<ApiResponse> toggleStatus(
            @Parameter(description = "The unique ID of the product", required = true)
            @PathVariable("id")
            @Positive(message = "{validation.invalid_id_format}")
            Long id,
            @Parameter(description = "Set to true to activate, false to deactivate", required = true)
            @RequestParam("activate") boolean activate) {
        return ResponseEntity.ok(adminService.toggleProductStatus(id, activate));
    }
}
