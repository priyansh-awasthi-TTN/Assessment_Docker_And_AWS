package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.ecommerceproject.enums.AddressType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AddressDTO {

    @Schema(
        description = "Complete address line including house number, street, and locality",
        example = "221B Baker Street",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.address_line_required}")
    @Size(min = 5, max = 50, message = "{validation.address_line_invalid}")
    String addressLine;

    @Schema(
        description = "City where the user resides",
        example = "Delhi",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.city_required}")
    @Size(min = 2, max = 20, message = "{validation.city_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.city_invalid}")
    String city;

    @Schema(
        description = "State of the address",
        example = "Uttar Pradesh",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.state_required}")
    @Size(min = 2, max = 20, message = "{validation.state_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.state_invalid}")
    String state;

    @Schema(
        description = "Country name",
        example = "India",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.country_required}")
    @Size(min = 2, max = 20, message = "{validation.country_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.country_invalid}")
    String country;

    @Schema(
        description = "Postal / ZIP code of the address",
        example = "110001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.zip_code_required}")
    @Pattern(regexp = "^[0-9]{6}$", message = "{validation.zip_code_invalid}")
    String zipCode;

    @Schema(
        description = "Address of a User",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.address_label_required}")
    AddressType label;
}
