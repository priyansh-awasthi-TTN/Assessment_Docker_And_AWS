package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
import com.example.ecommerceproject.enums.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@Schema(description = "Address update request")
public class AddressPartialUpdateRequestDTO {

    @Schema(description = "Address line", example = "221B Baker Street")
    @Size(max = 255, message = "{validation.address_line_invalid}")
    String addressLine;

    @Schema(description = "City", example = "Delhi")
    @Size(max = 40, message = "{validation.city_invalid}")
    String city;

    @Schema(description = "State", example = "Uttar Pradesh")
    @Size(max = 40, message = "{validation.state_invalid}")
    String state;

    @Schema(description = "Country", example = "India")
    @Size(max = 45, message = "{validation.country_invalid}")
    String country;

    @Schema(description = "ZIP code", example = "110001")
    @Size(max = 6, message = "{validation.zip_code_invalid}")
    String zipCode;

    @Schema(description = "Address label", example = "OFFICE")
    AddressType label;
}
