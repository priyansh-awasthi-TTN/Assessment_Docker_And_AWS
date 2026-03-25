package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Schema(description = "Seller registration request")
@FieldDefaults(level = PRIVATE)
public class SellerRegisterRequestDTO extends BaseRegistrationDTO{

    @NotBlank(message = "{validation.gst_required}")
    @Pattern(
        regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{3}$",
        message = "{validation.gst_invalid}"
    )
    @Schema(example = "22AAAAA0000A1Z5", description = "GST number of the seller")
    String gst;

    @NotBlank(message = "{validation.company_name_required}")
    @Schema(example = "ABC Electronics Pvt Ltd", description = "Seller company name")
    String companyName;

    @NotBlank(message = "{validation.company_contact_required}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_invalid}")
    @Schema(example = "9876543210", description = "Company contact number")
    String companyContact;

    @Schema(
        description = "Address",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.address_required}")
    @Valid
    AddressDTO address;
}
