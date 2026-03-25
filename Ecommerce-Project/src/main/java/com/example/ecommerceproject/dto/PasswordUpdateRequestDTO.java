package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Schema(description = "Password update request")
public class PasswordUpdateRequestDTO {

    @Schema(example = "Password@123", description = "New password")
    @NotBlank(message = "{validation.password_required}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$",
        message = "{validation.password_strength}")
    String password;

    @Schema(example = "Password@123", description = "Confirm new password")
    @NotBlank(message = "{validation.confirm_password_required}")
    String confirmPassword;
}
