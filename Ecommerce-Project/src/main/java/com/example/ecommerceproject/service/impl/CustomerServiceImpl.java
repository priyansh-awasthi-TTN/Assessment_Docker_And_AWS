package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.AddressResponseDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryFilterDetailsDTO;
import com.example.ecommerceproject.dto.CategoryMetadataDTO;
import com.example.ecommerceproject.dto.CategoryResponseDTO;
import com.example.ecommerceproject.dto.CustomerProductListResponseDTO;
import com.example.ecommerceproject.dto.CustomerProductViewResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileUpdateRequestDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.VariationDetailsDTO;
import com.example.ecommerceproject.dto.VariationListDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.Customer;
import com.example.ecommerceproject.entity.Product;
import com.example.ecommerceproject.entity.ProductVariations;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.enums.AddressType;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CategoryRepository;
import com.example.ecommerceproject.repository.CustomerRepository;
import com.example.ecommerceproject.repository.ProductRepository;
import com.example.ecommerceproject.service.CustomerService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerServiceImpl implements CustomerService {

    final CustomerRepository customerRepository;
    final AddressRepository addressRepository;
    final CategoryRepository categoryRepository;
    final ProductRepository productRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final MessageService messageService;
    final ModelMapper mapper;
    final UserSessionServiceImpl userSessionServiceImpl;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponseDTO getProfile(Long userId) {
        Customer customer = getActiveCustomerByUserId(userId);
        User user = customer.getUser();

        CustomerProfileResponseDTO dto = mapper.map(customer, CustomerProfileResponseDTO.class);
        dto.setId(userId);
        mapper.map(user, dto);
        dto.setId(user.getId());
        dto.setImage(computeImageUrl(userId, customer));

        return dto;
    }

    @Override
    @Transactional
    public ApiResponseDTO updateProfile(Long userId, CustomerProfileUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Customer customer = getActiveCustomerByUserId(userId);
        User user = customer.getUser();

        mapper.map(dto, user);
        mapper.map(dto, customer);

        return new ApiResponseDTO(messageService.get(MessageKeys.CUSTOMER_PROFILE_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto) {
        validateUserAccess(userId);
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH), 400);
        }

        Customer customer = getActiveCustomerByUserId(userId);
        User user = customer.getUser();

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        userSessionServiceImpl.revokeAllRefreshTokens(user);
        emailService.sendPasswordChangedEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.CUSTOMER_PASSWORD_UPDATED), 200);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddresses(Long userId) {
        validateUserAccess(userId);
        User user = getActiveCustomerByUserId(userId).getUser();

        return addressRepository.findByUserAndUserIsDeletedFalse(user)
                .stream()
                .map(address -> mapper.map(address, AddressResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public ApiResponseDTO addAddress(Long userId, AddressDTO dto) {
        validateUserAccess(userId);
        User user = getActiveCustomerByUserId(userId).getUser();
        if (dto.getLabel() != AddressType.HOME) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_INVALID_CUSTOMER_ADDRESS_LABEL), 400);
        }

        Address address = mapper.map(dto, Address.class);
        address.setUser(user);
        addressRepository.save(address);

        return new ApiResponseDTO(messageService.get(MessageKeys.CUSTOMER_ADDRESS_ADDED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updateAddress(Long userId, Long addressId, AddressPartialUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Address address = getValidAddressForUser(userId, addressId);

        mapper.map(dto, address);

        return new ApiResponseDTO(messageService.get(MessageKeys.CUSTOMER_ADDRESS_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO deleteAddress(Long userId, Long addressId) {
        validateUserAccess(userId);
        Address address = getValidAddressForUser(userId, addressId);
        addressRepository.delete(address);

        return new ApiResponseDTO(messageService.get(MessageKeys.CUSTOMER_ADDRESS_DELETED), 200);
    }

    private Customer getActiveCustomerByUserId(Long userId) {
        try {
            Customer customer = customerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.ERROR_CUSTOMER_NOT_FOUND), 404));

            if (!customer.getUser().isActive()) {
                throw new ApiException(messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED), 400);
            }
            return customer;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.ERROR_CUSTOMER_NOT_FOUND), 404);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getCategories(Long categoryId) {
        List<Category> categories;

        if (categoryId == null) {
            categories = categoryRepository.findByParentCategoryIsNull();
        } else {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ApiException(messageService.get(MessageKeys.INVALID_CATEGORY_ID), 400);
            }
            categories = categoryRepository.findByParentCategoryId(categoryId);
        }

        return categories.stream().map(cat -> {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryFilterDetailsDTO getCategoryFilteringDetails(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(MessageKeys.INVALID_CATEGORY_ID, 400));

        CategoryFilterDetailsDTO filterDTO = new CategoryFilterDetailsDTO();

        List<CategoryMetadataDTO> metadataList = category.getFieldValues().stream().map(fv -> {
            CategoryMetadataDTO metaDTO = new CategoryMetadataDTO();
            metaDTO.setMetadataFieldId(fv.getMetadataField().getId());
            metaDTO.setFieldName(fv.getMetadataField().getName());
            metaDTO.setPossibleValues(fv.getValue());
            return metaDTO;
        }).collect(Collectors.toList());

        filterDTO.setMetadataFields(metadataList);

        List<Long> allRelatedCategoryIds = new ArrayList<>();
        collectCategoryIdsRecursively(category, allRelatedCategoryIds);

        filterDTO.setBrands(productRepository.findDistinctBrandsByCategoryIds(allRelatedCategoryIds));

        Double minPrice = productRepository.findMinPriceByCategoryIds(allRelatedCategoryIds);
        Double maxPrice = productRepository.findMaxPriceByCategoryIds(allRelatedCategoryIds);

        filterDTO.setMinPrice(minPrice != null ? minPrice : 0.0);
        filterDTO.setMaxPrice(maxPrice != null ? maxPrice : 0.0);

        return filterDTO;
    }

    @Transactional(readOnly = true)
    public CustomerProductViewResponseDTO getProductDetails(Long productId) {
        Product product = productRepository.findByIdAndIsDeletedFalseAndIsActiveTrue(productId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.PRODUCT_NOT_FOUND), 400));

        List<ProductVariations> activeVariations = product.getProductVariationList().stream()
                .filter(ProductVariations::getIsActive)
                .collect(Collectors.toList());

        if (activeVariations.isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_VARIATIONS_NOT_AVAILABLE), 400);
        }

        CustomerProductViewResponseDTO response = mapper.map(product, CustomerProductViewResponseDTO.class);
        response.setVariations(activeVariations.stream()
                .map(variation -> {
                    VariationDetailsDTO dto = mapper.map(variation, VariationDetailsDTO.class);

                    int variationNumber = getVariationNumberFromId(variation.getId());

                    if (variation.getPrimaryImageName() != null) {
                        dto.setPrimaryImageUrl("/api/user/products/" + productId + "/images/" + variation.getPrimaryImageName());
                    }

                    dto.setSecondaryImageUrls(generateSecondaryImageUrls(productId, variationNumber));

                    return dto;
                })
                .collect(Collectors.toList()));

        return response;
    }

    @Transactional(readOnly = true)
    public Page<CustomerProductListResponseDTO> getAllProductsByCategory(
            Long categoryId, int offset, int max, String sort, String order) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.INVALID_CATEGORY_ID), 400));

        List<Long> categoryIds = extractAllCategoryIds(category);

        Pageable pageable = PageRequest.of(offset, max, Sort.Direction.fromString(order.toUpperCase()), sort);

        return productRepository.findActiveProductsWithActiveVariationsByCategoryIds(categoryIds, pageable)
                .map(this::mapToProductListResponse);
    }

    @Transactional(readOnly = true)
    public Page<CustomerProductListResponseDTO> getSimilarProducts(
            Long productId, int offset, int max, String sort, String order) {
        Product baseProduct = productRepository.findByIdAndIsDeletedFalseAndIsActiveTrue(productId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.PRODUCT_NOT_FOUND), 400));

        Long categoryId = baseProduct.getCategory().getId();

        Pageable pageable = PageRequest.of(offset, max, Sort.Direction.fromString(order.toUpperCase()), sort);

        return productRepository.findSimilarProducts(categoryId, productId, pageable)
                .map(this::mapToProductListResponse);
    }

    private List<Long> extractAllCategoryIds(Category category) {
        List<Long> ids = new ArrayList<>();
        ids.add(category.getId());

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            for (Category child : category.getSubCategories()) {
                ids.addAll(extractAllCategoryIds(child));
            }
        }
        return ids;
    }

    private CustomerProductListResponseDTO mapToProductListResponse(Product product) {
        CustomerProductListResponseDTO dto = mapper.map(product, CustomerProductListResponseDTO.class);

        List<VariationListDTO> variationDTOs = product.getProductVariationList().stream()
                .filter(ProductVariations::getIsActive)
                .map(variation -> {
                    VariationListDTO variationDto = mapper.map(variation, VariationListDTO.class);

                    if (variation.getPrimaryImageName() != null) {
                        variationDto.setPrimaryImageUrl("/api/user/products/" + product.getId() + "/images/" + variation.getPrimaryImageName());
                    }

                    return variationDto;
                })
                .collect(Collectors.toList());

        dto.setVariations(variationDTOs);
        return dto;
    }

    private String computeImageUrl(Long userId, Customer customer) {
        try {
            Path userDir = Paths.get("uploads/users/");
            if (!Files.exists(userDir)) {
                return null;
            }

            Long[] idsToTry = { userId, customer != null ? customer.getId() : null };

            for (Long id : idsToTry) {
                if (id == null) continue;

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

    private void collectCategoryIdsRecursively(Category category, List<Long> ids) {
        ids.add(category.getId());

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            for (Category child : category.getSubCategories()) {
                collectCategoryIdsRecursively(child, ids);
            }
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

    private Address getValidAddressForUser(Long userId, Long addressId) {
        getActiveCustomerByUserId(userId);

        Address address = addressRepository.findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.ERROR_ADDRESS_NOT_FOUND), 404));

        if (!address.getUser().getId().equals(userId)) {
            throw new ApiException(messageService.get(MessageKeys.ERROR_ADDRESS_PERMISSION_DENIED), 403);
        }
        return address;
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

            System.err.println("Error generating secondary image URLs for product " + productId +
                             " variation " + variationNumber + ": " + e.getMessage());
        }
        return secondaryUrls;
    }

}
