package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.ImageUploadService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = PRIVATE)
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${aws.bucket}")
    private String bucket;

    final UserRepository userRepository;
    final MessageService messageService;
    static final String UPLOAD_DIR = "uploads/users/";
    static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    final S3Client s3Client;

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO uploadUserImage(Long userId, MultipartFile file) {
        try {
            Long authenticatedUserId = getCurrentUserId();
            if (!userId.equals(authenticatedUserId)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }

            userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(MessageKeys.ERROR_USER_NOT_FOUND, 404));
            validateFile(file);

            String extension = getFileExtension(file.getOriginalFilename());

            String key = "users/" + userId + "/profile." + extension;
            deleteExistingImage(userId);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return new ApiResponseDTO(messageService.get(MessageKeys.IMAGE_UPLOAD_SUCCESS), 204);

        } catch (IOException e) {
            log.error("Failed to upload image for user ID: {}", userId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            log.error("Unexpected error during image upload for user ID: {}", userId, e);
            throw new ApiException(messageService.get(MessageKeys.IMAGE_UPLOAD_FAILED), 500);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getUserImage(Long userId) {
        try {
            Long authenticatedUserId = getCurrentUserId();
            String userRole = getCurrentUserRole();

            if (!"ADMIN".equals(userRole) && !userId.equals(authenticatedUserId)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }

            String[] extensions = {"jpg", "jpeg", "png"};
            for (String ext : extensions) {
                String key = "users/" + userId + "/profile." + ext;
                try {
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

                    ResponseBytes<GetObjectResponse> response = s3Client.getObject(
                            getObjectRequest,
                            ResponseTransformer.toBytes()
                    );

                    byte[] imageBytes = response.asByteArray();
                    return new ByteArrayResource(imageBytes);

                } catch (S3Exception e) {
                    if (e.statusCode() == 404) continue;
                    throw e;
                }
            }

            throw new ApiException(messageService.get(MessageKeys.IMAGE_NOT_FOUND), 404);

        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.ERROR_INTERNAL_SERVER, 500);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getProductImage(Long productId, String filename) {
        try {

            Path imagePath = Paths.get("uploads/products/" + productId).resolve(filename);

            if (!Files.exists(imagePath)) {
                throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
            }

            String fileExtension = getFileExtension(filename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
            }

            return new FileSystemResource(imagePath);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.ERROR_INTERNAL_SERVER, 500);
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        }
    }

    private String getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getAuthorities() != null) {
                return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(MessageKeys.IMAGE_FILE_REQUIRED, 400);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(MessageKeys.IMAGE_FILE_TOO_LARGE, 400);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FILENAME, 400);
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FILENAME, 400);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private void deleteExistingImage(Long userId) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            for (String extension : ALLOWED_EXTENSIONS) {
                Path existingFile = uploadPath.resolve(userId + "." + extension);
                if (Files.exists(existingFile)) {
                    Files.delete(existingFile);
                    log.info("Deleted existing image: {}", existingFile.getFileName());
                }
            }
        } catch (IOException e) {
            log.warn("Failed to delete existing image for user ID: {}", userId, e);
        }
    }
}
