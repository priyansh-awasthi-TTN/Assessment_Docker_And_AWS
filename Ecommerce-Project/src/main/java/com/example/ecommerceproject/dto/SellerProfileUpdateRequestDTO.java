package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class SellerProfileUpdateRequestDTO {

    String firstName;
    String middleName;
    String lastName;
    String companyName;

    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_invalid}")
    String companyContact;

}
