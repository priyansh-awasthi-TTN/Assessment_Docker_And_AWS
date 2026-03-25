package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public abstract class BaseRegistrationDTO {

    @Schema(example = "user@example.com", description = "Email address")
    @NotBlank(message = "{validation.email_required}")
    @Email(message = "{validation.email_invalid}")
    String email;

    @Schema(example = "Password@123", description = "Password")
    @NotBlank(message = "{validation.password_required}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$",
        message = "{validation.password_strength}")
    String password;

    @Schema(example = "Password@123", description = "Confirm password")
    @NotBlank(message = "{validation.confirm_password_required}")
    String confirmPassword;

    @Schema(example = "John", description = "First name")
    @NotBlank(message = "{validation.first_name_required}")
    @Size(max = 30)
    String firstName;

    @Schema(example = "Martin", description = "Middle name")
    @Size(max = 30)
    String middleName;

    @Schema(example = "Doe", description = "Last name")
    @NotBlank(message = "{validation.last_name_required}")
    @Size(max = 30)
    String lastName;
}
