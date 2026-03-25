package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
@Schema(description = "Reset password request")
public class ResetPasswordRequestDTO {

    @Schema(description = "Password reset token from email link")
    @NotBlank(message = "{validation.reset_token_required}")
    String token;

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
