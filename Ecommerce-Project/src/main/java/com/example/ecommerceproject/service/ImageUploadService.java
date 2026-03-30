package com.example.ecommerceproject.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;

public interface ImageUploadService {
    ApiResponseDTO uploadUserImage(Long userId, MultipartFile file);
    Resource getUserImage(Long userId);
    Resource getProductImage(Long productId, String filename);
}
