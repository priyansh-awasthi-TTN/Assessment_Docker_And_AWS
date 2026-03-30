package com.example.ecommerceproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.service.ImageUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Image Management", description = "Common APIs for Customers, Sellers and Admins to manage profile images")
@SecurityRequirement(name = "bearerAuth")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ImageUploadController.class);

    @Operation(summary = "Upload Profile Image",
               description = "Upload a profile image for the user. Supports JPG, JPEG, PNG formats. Maximum file size: 5MB")
    @PostMapping("/{userId}/image")
    public ResponseEntity<ApiResponseDTO> uploadImage(
            @Parameter(description = "User ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,

            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("image")
            MultipartFile file) {

        System.out.println("CONTROLLER HIT");
        return ResponseEntity.ok(imageUploadService.uploadUserImage(userId, file));
    }

    @Operation(summary = "Get User Image",
               description = "Retrieve user profile image. Customers/Sellers can only access their own images. Admins can access any user's image.")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Resource> getUserImage(
            @Parameter(description = "User ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId) {

        Resource resource = imageUploadService.getUserImage(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + userId + "/profile" + "\"")
                .body(resource);
    }

    @Operation(summary = "Get Product Image",
               description = "Retrieve product variation image.")
    @GetMapping("{userId}/products/{productId}v{variationId}/image")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> getProductImage(
            @Parameter(description = "User ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,

            @Parameter(description = "Product ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long productId,

            @Parameter(description = "Product ID", required = true)
            @PathVariable
            @Positive(message = "{validation.invalid_id_format}")
            Long variationId) {

        Resource resource = imageUploadService.getProductImage(userId, productId, variationId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + userId + "/product/" + productId + "/image" + "\"")
                .body(resource);
    }
}
