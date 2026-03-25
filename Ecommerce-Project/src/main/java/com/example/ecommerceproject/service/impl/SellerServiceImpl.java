package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.repository.ProductRepository;
import com.example.ecommerceproject.repository.ProductVariationRepository;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.ProductCreateRequest;
import com.example.ecommerceproject.dto.ProductResponseDTO;
import com.example.ecommerceproject.dto.ProductUpdateRequest;
import com.example.ecommerceproject.dto.ProductVariationCreateRequest;
import com.example.ecommerceproject.dto.ProductVariationResponse;
import com.example.ecommerceproject.dto.ProductVariationUpdateRequest;
import com.example.ecommerceproject.dto.SellerCategoryResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.CategoryMetadataFieldValues;
import com.example.ecommerceproject.entity.Product;
import com.example.ecommerceproject.entity.ProductVariations;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CategoryMetadataFieldValuesRepository;
import com.example.ecommerceproject.repository.CategoryRepository;
import com.example.ecommerceproject.repository.SellerRepository;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.specs.ProductSpecifications;
import com.example.ecommerceproject.util.MessageKeys;
import com.example.ecommerceproject.enums.AddressType;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
@Slf4j
public class SellerServiceImpl implements SellerService {

    final SellerRepository sellerRepository;
    final ProductRepository productRepository;
    final AddressRepository addressRepository;
    final CategoryRepository categoryRepository;
    final CategoryMetadataFieldValuesRepository metadataFieldRepository;
    final ProductVariationRepository variationRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final MessageService messageService;
    final UserSessionService userSessionService;
    final ModelMapper modelMapper;
    final ObjectMapper objectMapper;
    final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDTO getProfile(Long userId) {
        validateUserAccess(userId);
        validateSellerRole();
        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();
        List<Address> addresses = addressRepository.findByUserAndUserIsDeletedFalse(user);
        Address address = addresses.isEmpty() ? new Address() : addresses.get(0);

        SellerProfileResponseDTO response = modelMapper.map(seller, SellerProfileResponseDTO.class);
        modelMapper.map(user, response);
        modelMapper.map(address, response);
        response.setId(user.getId());
        response.setImage(computeImageUrl(user.getId(), seller));
        response.setAddressId(address.getId());

        return response;
    }

    @Override
    @Transactional
    public ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Seller seller = getActiveSellerByUserId(userId);

        modelMapper.map(dto, seller);
        modelMapper.map(dto, seller.getUser());

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PROFILE_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto) {
        validateUserAccess(userId);
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_PASSWORD_DO_NOT_MATCH), 400);
        }

        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        userSessionService.revokeAllRefreshTokens(user);

        emailService.sendPasswordChangedEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PASSWORD_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updateAddress(Long userId, AddressPartialUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Seller seller = getActiveSellerByUserId(userId);
        Address address = addressRepository.findByUserAndUserIsDeletedFalse(seller.getUser()).stream().findFirst()
                .orElseThrow(
                        () -> new ApiException(messageService.get(MessageKeys.ERROR_ADDRESS_NOT_FOUND), 400));

        if (dto.getLabel() != null) {
            validateSellerAddressLabel(dto.getLabel());
        }

        modelMapper.map(dto, address);

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_ADDRESS_UPDATED), 200);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerCategoryResponseDTO> getAllLeafCategories() {
        List<Category> leafNodes = categoryRepository.findAllLeafNodes();
        return leafNodes.stream().map(this::mapToSellerCategoryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse createProduct(ProductCreateRequest dto) {
        Seller seller = getActiveSellerByUserId(getCurrentUserId());
        Long sellerId = seller.getId();

        if (!categoryRepository.isLeafNode(dto.getCategoryId())) {
            throw new ApiException(messageService.get(MessageKeys.CATEGORY_MUST_BE_VALID_LEAF), 400);
        }
        if (productRepository.existsByNameAndBrandAndCategory_IdAndSeller_Id(dto.getName(), dto.getBrand(),
                dto.getCategoryId(), sellerId)) {
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_UNIQUE_FOR_BRAND_AND_CATEGORY), 400);
        }

        Product product = modelMapper.map(dto, Product.class);

        product.setSeller(seller);
        Category category = categoryRepository.getReferenceById(dto.getCategoryId());
        product.setCategory(category);
        product.setIsActive(false);
        productRepository.save(product);

        try {
            String sellerName = seller.getUser().getFirstName() + " " + seller.getUser().getLastName();
            String sellerEmail = seller.getUser().getEmail();
            emailService.sendProductCreatedNotificationToAdmin(
                    sellerName,
                    sellerEmail,
                    product.getName(),
                    category.getName(),
                    product.getBrand());
        } catch (Exception e) {
            log.warn("Failed to send product creation notification email for product: {}", product.getName(), e);
        }

        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_ADDED_SUCCESSFULLY), product.getId());
    }

    @Override
    @Transactional
    public ApiResponse createProductVariation(Long productId, String variationJson, MultipartFile primaryImage,
            MultipartFile[] secondaryImages) {

        ProductVariationCreateRequest dto;
        try {
            dto = objectMapper.readValue(variationJson, ProductVariationCreateRequest.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_INVALID_JSON_FORMAT), 400);
        }

        Set<ConstraintViolation<ProductVariationCreateRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<ProductVariationCreateRequest> violation : violations) {
                if (errorMessage.length() > 0) {
                    errorMessage.append("; ");
                }
                errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            }
            throw new ApiException(errorMessage.toString(), 400);
        }

        try {
            Seller seller = getActiveSellerByUserId(getCurrentUserId());
            Product product = productRepository.findByIdAndSeller_IdAndIsDeletedFalse(productId, seller.getId())
                    .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.PRODUCT_NOT_FOUND), 400));

            if (!product.getIsActive()) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_ACTIVE_TO_ADD_VARIATION), 400);
            }

            validateVariationMetadata(product.getCategory().getId(), productId, dto.getMetadata());
            validateNoDuplicateVariation(productId, dto.getMetadata());

            int nextVariationNumber = getNextVariationNumber(productId);

            String primaryImageName = null;
            if (primaryImage != null && !primaryImage.isEmpty()) {
                primaryImageName = saveProductImage(productId, nextVariationNumber, primaryImage, true);
            }

            if (secondaryImages != null && secondaryImages.length > 0) {
                saveProductSecondaryImages(productId, nextVariationNumber, secondaryImages);
            }

            ProductVariations variations = modelMapper.map(dto, ProductVariations.class);
            variations.setProduct(product);
            variations.setIsActive(true);
            variations.setPrimaryImageName(primaryImageName);
            variationRepository.save(variations);

            return new ApiResponse(messageService.get(MessageKeys.PRODUCT_VARIATION_CREATED_SUCCESSFULLY),
                    variations.getId());

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating product variation for product {}: {}", productId, e.getMessage(), e);
            throw new ApiException(messageService.get(MessageKeys.ERROR_INTERNAL_SERVER), 500);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Map<String, String> params) {
        Seller seller = getActiveSellerByUserId(getCurrentUserId());
        int page = Integer.parseInt(params.getOrDefault("offset", "0"));
        int size = Integer.parseInt(params.getOrDefault("max", "10"));
        String sortBy = params.getOrDefault("sort", "id");
        Sort.Direction direction = Sort.Direction.fromString(params.getOrDefault("order", "ASC"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Product> spec = ProductSpecifications.buildFilter(params);
        params.put("sellerId", seller.getId().toString());

        return productRepository.findAll(spec, pageable)
                .map(product -> {
                    ProductResponseDTO response = modelMapper.map(product, ProductResponseDTO.class);

                    if (product.getProductVariationList() != null && !product.getProductVariationList().isEmpty()) {
                        List<ProductVariationResponse> variationDTOs = product.getProductVariationList().stream()
                                .map(variation -> {
                                    ProductVariationResponse dto = modelMapper.map(variation, ProductVariationResponse.class);

                                    dto.setProductId(product.getId());

                                    int variationNumber = getVariationNumberFromId(variation.getId());

                                    if (variation.getPrimaryImageName() != null) {
                                        dto.setPrimaryImageUrl("/api/user/products/" + product.getId() + "/images/" + variation.getPrimaryImageName());
                                    }

                                    dto.setSecondaryImageUrls(generateSecondaryImageUrls(product.getId(), variationNumber));

                                    return dto;
                                })
                                .collect(Collectors.toList());
                        response.setVariations(variationDTOs);
                    }

                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariationResponse> getProductVariations(Long productId, int offset, int max, String sort,
            String order) {
        Seller seller = getActiveSellerByUserId(getCurrentUserId());

        productRepository.findByIdAndSeller_IdAndIsDeletedFalse(productId, seller.getId())
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.INVALID_PRODUCT_ID), 400));

        Pageable pageable = PageRequest.of(offset, max, Sort.Direction.fromString(order.toUpperCase()), sort);
        return variationRepository.findAllByProductId(productId, pageable)
                .map(variation -> {
                    ProductVariationResponse dto = modelMapper.map(variation, ProductVariationResponse.class);

                    dto.setProductId(productId);

                    int variationNumber = getVariationNumberFromId(variation.getId());

                    if (variation.getPrimaryImageName() != null) {
                        dto.setPrimaryImageUrl("/api/user/products/" + productId + "/images/" + variation.getPrimaryImageName());
                    }

                    dto.setSecondaryImageUrls(generateSecondaryImageUrls(productId, variationNumber));

                    return dto;
                });
    }

    @Override
    @Transactional
    public ApiResponse deleteProduct(Long productId) {
        Seller seller = getActiveSellerByUserId(getCurrentUserId());

        Product product = productRepository.findByIdAndSeller_IdAndIsDeletedFalse(productId, seller.getId())
                .orElseThrow(
                        () -> new ApiException(messageService.get(MessageKeys.INVALID_PRODUCT_ID), 400));
        productRepository.delete(product);
        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_DELETED_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ApiResponse updateProduct(Long productId, ProductUpdateRequest dto) {
        Seller seller = getActiveSellerByUserId(getCurrentUserId());
        Long sellerId = seller.getId();

        Product existingProduct = productRepository.findByIdAndSeller_IdAndIsDeletedFalse(productId, sellerId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.INVALID_PRODUCT_ID), 400));

        if (dto.getName() != null && !dto.getName().equals(existingProduct.getName())) {
            if (productRepository.existsByNameAndBrandAndCategory_IdAndSeller_Id(
                    dto.getName(), existingProduct.getBrand(), existingProduct.getCategory().getId(), sellerId)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_NAME_EXISTS), 400);
            }
        }

        modelMapper.map(dto, existingProduct);
        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_UPDATED_SUCCESSFULLY), existingProduct.getId());
    }

    @Override
    @Transactional
    public ApiResponse updateProductVariation(Long productId, Long variationId, String variationJson,
            MultipartFile primaryImage, MultipartFile[] secondaryImages) {

        ProductVariationUpdateRequest dto;
        try {
            dto = objectMapper.readValue(variationJson, ProductVariationUpdateRequest.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_INVALID_JSON_FORMAT), 400);
        }

        Set<ConstraintViolation<ProductVariationUpdateRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<ProductVariationUpdateRequest> violation : violations) {
                if (errorMessage.length() > 0) {
                    errorMessage.append("; ");
                }
                errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            }
            throw new ApiException(errorMessage.toString(), 400);
        }

        try {
            Seller seller = getActiveSellerByUserId(getCurrentUserId());
            Product product = productRepository.findByIdAndSeller_IdAndIsDeletedFalse(productId, seller.getId())
                    .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.PRODUCT_NOT_FOUND), 400));

            if (!product.getIsActive()) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_ACTIVE), 400);
            }

            ProductVariations existingVariation = variationRepository.findByIdAndProductId(variationId, productId)
                    .orElseThrow(
                            () -> new ApiException(messageService.get(MessageKeys.PRODUCT_VARIATION_NOT_FOUND), 400));

            if (dto.getQuantityAvailable() != null) {
                existingVariation.setQuantityAvailable(dto.getQuantityAvailable());
            }
            if (dto.getPrice() != null) {
                existingVariation.setPrice(dto.getPrice());
            }
            if (dto.getIsActive() != null) {
                existingVariation.setIsActive(dto.getIsActive());
            }
            if (dto.getMetadata() != null) {
                validateVariationMetadata(product.getCategory().getId(), productId, dto.getMetadata());
                existingVariation.setMetadata(dto.getMetadata());
            }

            int variationNumber = getVariationNumber(variationId);

            if (primaryImage != null && !primaryImage.isEmpty()) {
                String primaryImageName = saveProductImage(productId, variationNumber, primaryImage, true);
                existingVariation.setPrimaryImageName(primaryImageName);
            }

            if (secondaryImages != null && secondaryImages.length > 0) {
                saveProductSecondaryImages(productId, variationNumber, secondaryImages);
            }

            variationRepository.save(existingVariation);
            return new ApiResponse(messageService.get(MessageKeys.PRODUCT_VARIATION_UPDATED_SUCCESSFULLY), variationId);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating product variation {} for product {}: {}", variationId, productId, e.getMessage(), e);
            throw new ApiException(messageService.get(MessageKeys.ERROR_INTERNAL_SERVER), 500);
        }
    }

    private void validateVariationMetadata(Long categoryId, Long productId, Map<String, String> incomingMetadata) {
        if (incomingMetadata == null || incomingMetadata.isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.VARIATION_MUST_HAVE_ONE_VALUE), 400);
        }

        List<CategoryMetadataFieldValues> allowedFieldsFromDb = metadataFieldRepository
                .findAllByCategory_Id(categoryId);
        if (allowedFieldsFromDb.isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_NO_METADATA_FIELDS), 400);
        }
        Map<String, List<String>> allowedMetadataMap = new HashMap<>();
        for (CategoryMetadataFieldValues cmfv : allowedFieldsFromDb) {
            String fieldName = cmfv.getMetadataField().getName().toLowerCase();
            String value = cmfv.getValue().trim().toLowerCase();
            allowedMetadataMap.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(value);
        }
        List<String> invalidFields = new ArrayList<>();
        List<String> invalidValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : incomingMetadata.entrySet()) {
            String inputKey = entry.getKey().trim().toLowerCase();
            String inputValue = entry.getValue().trim().toLowerCase();

            if (!allowedMetadataMap.containsKey(inputKey)) {
                invalidFields.add(entry.getKey());
            } else {
                List<String> allowedValues = allowedMetadataMap.get(inputKey);
                if (!allowedValues.contains(inputValue)) {
                    List<String> originalCaseValues = metadataFieldRepository.findAllByCategory_Id(categoryId)
                            .stream()
                            .filter(cmfv -> cmfv.getMetadataField().getName().equalsIgnoreCase(entry.getKey()))
                            .map(cmfv -> cmfv.getValue())
                            .collect(Collectors.toList());

                    invalidValues
                            .add(entry.getKey() + ": " + entry.getValue() + " (allowed: " + originalCaseValues + ")");
                }
            }
        }
        if (!invalidFields.isEmpty()) {
            throw new ApiException(
                    messageService.get(MessageKeys.PRODUCT_INVALID_METADATA_FIELDS, invalidFields),
                    400);
        }
        if (!invalidValues.isEmpty()) {
            throw new ApiException(
                    messageService.get(MessageKeys.PRODUCT_INVALID_METADATA_VALUES, invalidValues),
                    400);
        }
        Page<ProductVariations> existingVariations = variationRepository.findAllByProductId(productId,
                PageRequest.of(0, 1));
        if (existingVariations.hasContent()) {
            Set<String> existingKeys = existingVariations.getContent().get(0).getMetadata().keySet();
            Set<String> newKeys = incomingMetadata.keySet();

            if (!existingKeys.equals(newKeys)) {
                throw new ApiException(
                        messageService.get(MessageKeys.PRODUCT_VARIATION_KEYS_MISMATCH, existingKeys),
                        400);
            }
        }
    }

    private void validateNoDuplicateVariation(Long productId, Map<String, String> newMetadata) {
        List<ProductVariations> existingVariations = variationRepository
                .findAllByProduct_IdAndProduct_IsActiveTrueAndProduct_IsDeletedFalse(productId);

        for (ProductVariations existing : existingVariations) {
            if (existing.getMetadata().equals(newMetadata)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_VARIATION_ALREADY_EXISTS), 400);
            }
        }
    }

    private SellerCategoryResponseDTO mapToSellerCategoryDTO(Category category) {
        SellerCategoryResponseDTO dto = new SellerCategoryResponseDTO();
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setParentChain(buildParentChain(category));

        List<CategoryMetadataDTO> metadataDTOs = category.getFieldValues().stream().map(fv -> {
            CategoryMetadataDTO metaDto = new CategoryMetadataDTO();
            metaDto.setMetadataFieldId(fv.getMetadataField().getId());
            metaDto.setFieldName(fv.getMetadataField().getName());
            metaDto.setPossibleValues(fv.getValue());
            return metaDto;
        }).collect(Collectors.toList());
        dto.setMetadataFields(metadataDTOs);

        return dto;
    }

    private String buildParentChain(Category category) {
        StringBuilder chain = new StringBuilder(category.getName());
        Category parent = category.getParentCategory();

        while (parent != null) {
            chain.insert(0, parent.getName() + " > ");
            parent = parent.getParentCategory();
        }

        return chain.toString();
    }

    private Seller getActiveSellerByUserId(Long userId) {
        try {
            Seller seller = sellerRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND), 404));

            if (!seller.getUser().isActive()) {
                throw new ApiException(messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED), 400);
            }

            return seller;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND), 404);
        }
    }

    private String computeImageUrl(Long userId, Seller seller) {
        try {
            Path userDir = Paths.get("uploads/users/");
            if (!Files.exists(userDir)) {
                return null;
            }

            Long[] idsToTry = { userId, seller != null ? seller.getId() : null };

            for (Long id : idsToTry) {
                if (id == null)
                    continue;

                String imageUrl = findImageForId(id);
                if (imageUrl != null) {
                    return imageUrl;
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String findImageForId(Long id) {
        try {
            Path userDir = Paths.get("uploads/users/");
            for (String extension : Arrays.asList("jpg", "jpeg", "png")) {
                Path imagePath = userDir.resolve(id + "." + extension);
                if (Files.exists(imagePath)) {
                    return "/api/user/" + id + "/image/" + id + "." + extension;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void validateSellerAddressLabel(AddressType label) {
        if (label == AddressType.HOME) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_INVALID_SELLER_ADDRESS_LABEL), 400);
        }
    }

    private void validateUserAccess(Long requestedUserId) {
        Long authenticatedUserId = getCurrentUserId();
        if (!requestedUserId.equals(authenticatedUserId)) {
            throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
            throw new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_AUTHENTICATED), 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_AUTHENTICATED), 401);
        }
    }

    private void validateSellerRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(messageService.get(MessageKeys.ROLE_SELLER)))) {
                throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
            }
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
        }
    }

    private int getNextVariationNumber(Long productId) {
        long count = variationRepository.countByProduct_IdAndProduct_IsDeletedFalse(productId);
        return (int) count + 1;
    }

    private int getVariationNumber(Long variationId) {
        return variationId.intValue();
    }

    private String saveProductImage(Long productId, int variationNumber, MultipartFile file, boolean isPrimary) {
        try {
            if (file.isEmpty()) {
                throw new ApiException(messageService.get(MessageKeys.IMAGE_FILE_REQUIRED), 400);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.matches("(?i).*\\.(jpg|jpeg|png)$")) {
                throw new ApiException(messageService.get(MessageKeys.IMAGE_INVALID_FORMAT), 400);
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = productId + "v" + variationNumber + extension;

            Path uploadDir = Paths.get("uploads/products/" + productId);
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException e) {
            throw new ApiException(messageService.get(MessageKeys.IMAGE_UPLOAD_FAILED), 500);
        }
    }

    private void saveProductSecondaryImages(Long productId, int variationNumber, MultipartFile[] files) {
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (!file.isEmpty()) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    if (originalFilename == null || !originalFilename.matches("(?i).*\\.(jpg|jpeg|png)$")) {
                        continue;
                    }

                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String filename = productId + "v" + variationNumber + "_" + (i + 1) + extension;

                    Path uploadDir = Paths.get("uploads/products/" + productId);
                    Files.createDirectories(uploadDir);

                    Path filePath = uploadDir.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    log.warn("Failed to save secondary image {}: {}", i, e.getMessage());
                }
            }
        }
    }

    private int getVariationNumberFromId(Long variationId) {

        return variationId.intValue();
    }

    private List<String> generateSecondaryImageUrls(Long productId, int variationNumber) {
        List<String> secondaryUrls = new ArrayList<>();
        try {
            Path productDir = Paths.get("uploads/products/" + productId);
            if (!Files.exists(productDir)) {
                return secondaryUrls;
            }

            String basePattern = productId + "v" + variationNumber + "_";

            for (int i = 1; i <= 10; i++) {
                boolean foundImage = false;
                for (String extension : Arrays.asList("jpg", "jpeg", "png")) {
                    String filename = basePattern + i + "." + extension;
                    Path imagePath = productDir.resolve(filename);
                    if (Files.exists(imagePath)) {
                        secondaryUrls.add("/api/user/products/" + productId + "/images/" + filename);
                        foundImage = true;
                        break;
                    }
                }

                if (!foundImage) {
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("Error generating secondary image URLs for product {} variation {}: {}",
                    productId, variationNumber, e.getMessage());
        }
        return secondaryUrls;
    }

}
