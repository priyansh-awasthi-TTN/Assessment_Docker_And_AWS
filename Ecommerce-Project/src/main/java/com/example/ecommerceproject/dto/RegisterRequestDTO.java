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
@Schema(description = "Customer registration request")
public class RegisterRequestDTO extends BaseRegistrationDTO {

    @Schema(example = "9876543210", description = "Customer phone number")
    @NotBlank(message = "{validation.phone_invalid}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_number_invalid}")
    String phoneNumber;
}
