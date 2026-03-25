package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ForgotPasswordRequestDTO {

    @Schema(example = "user@example.com", description = "Registered email address")
    @NotBlank(message = "{validation.email_required}")
    @Email(message = "{validation.email_invalid}")
    String email;
}
