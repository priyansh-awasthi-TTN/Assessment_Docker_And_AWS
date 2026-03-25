package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ValidationErrorResponseDTO {
    LocalDateTime timestamp;
    String message;
    List<FieldErrorDTO> errors;
    int status;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FieldErrorDTO {
        String field;
        String defaultMessage;
    }
}
