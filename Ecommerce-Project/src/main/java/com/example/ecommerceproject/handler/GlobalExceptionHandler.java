package com.example.ecommerceproject.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.example.ecommerceproject.dto.ValidationErrorResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException apiException) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(apiException.getMessage(), null, apiException.getMessage(), locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", apiException.getStatus());
        return new ResponseEntity<>(response, HttpStatus.valueOf(apiException.getStatus()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_ACCOUNT_LOCKED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<?> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_PASSWORD_EXPIRED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_INVALID_CREDENTIALS, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_INVALID_CREDENTIALS, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String message = messageService.get(MessageKeys.VALIDATION_FAILED);
        List<ValidationErrorResponseDTO.FieldErrorDTO> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> new ValidationErrorResponseDTO.FieldErrorDTO(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                message,
                errors,
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        String message = messageService.get(MessageKeys.ERROR_INTERNAL_SERVER);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", 500);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get("validation.failed", null, locale);
        List<ValidationErrorResponseDTO.FieldErrorDTO> errors = ex.getConstraintViolations()
            .stream()
            .map(violation -> {
                String fieldPath = violation.getPropertyPath().toString();
                return new ValidationErrorResponseDTO.FieldErrorDTO(fieldPath, violation.getMessage());
            })
            .collect(Collectors.toList());

        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                message,
                errors,
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.JWT_TOKEN_EXPIRED, null, "Token has expired", locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.JWT_TOKEN_MALFORMED, null, "Invalid token format", locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<?> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.JWT_TOKEN_INVALID, null, "Unsupported token", locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.JWT_TOKEN_INVALID, null, "Invalid token", locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message;
        if (ex.getMessage() != null && ex.getMessage().contains("category_metadata_field_values")) {
            message = messageService.get(MessageKeys.METADATA_FIELD_VALUE_DUPLICATE, null, locale);
        } else {
            message = messageService.get("validation.constraint_violation", null, "Data constraint violation", locale);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message;
        String parameterName = ex.getName();
        if (parameterName != null && parameterName.toLowerCase().endsWith("id")) {
            String idType = parameterName.substring(0, parameterName.length() - 2);
            idType = idType.substring(0, 1).toUpperCase() + idType.substring(1);
            message = messageService.get(MessageKeys.VALIDATION_INVALID_ID_FORMAT,
                new Object[]{idType},
                idType + " ID must be a positive number",
                locale);
        } else {
            String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid type";
            message = messageService.get(MessageKeys.VALIDATION_INVALID_PARAMETER_TYPE,
                new Object[]{parameterName, expectedType, ex.getValue()},
                "Invalid " + parameterName + ". Expected " + expectedType + " but received: " + ex.getValue(),
                locale);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String partName = ex.getRequestPartName();
        String message;

        if ("image".equals(partName)) {
            message = messageService.get(MessageKeys.IMAGE_FILE_REQUIRED, null, "Image file is required", locale);
        } else {
            message = messageService.get(MessageKeys.VALIDATION_MISSING_REQUIRED_PART,
                new Object[]{partName},
                "Required part '" + partName + "' is missing",
                locale);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();

        if (resourcePath != null && resourcePath.startsWith("images/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Image not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("path", "/" + resourcePath);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Resource not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", "/" + resourcePath);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get("validation.multipart_required", null, "Request must be multipart/form-data", locale);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.apache.tomcat.util.http.InvalidParameterException.class)
    public ResponseEntity<?> handleInvalidParameterException(org.apache.tomcat.util.http.InvalidParameterException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get("validation.invalid_url_characters", null,
            "Invalid characters in URL. Please ensure all special characters are properly URL encoded.", locale);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String method = ex.getMethod();
        String[] supportedMethods = ex.getSupportedMethods();

        String message;
        if (supportedMethods != null && supportedMethods.length > 0) {
            String supported = String.join(", ", supportedMethods);
            message = messageService.get("validation.method_not_supported_with_allowed",
                new Object[]{method, supported},
                "HTTP method '" + method + "' is not supported for this endpoint. Allowed methods: " + supported,
                locale);
        } else {
            message = messageService.get("validation.method_not_supported",
                new Object[]{method},
                "HTTP method '" + method + "' is not supported for this endpoint.",
                locale);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        response.put("method", method);
        if (supportedMethods != null) {
            response.put("allowedMethods", supportedMethods);
        }

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
