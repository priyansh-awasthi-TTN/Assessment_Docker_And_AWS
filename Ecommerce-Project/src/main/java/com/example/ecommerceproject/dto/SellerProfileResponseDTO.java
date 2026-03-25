package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class SellerProfileResponseDTO {
    Long id;
    String firstName;
    String middleName;
    String lastName;
    boolean isActive;
    String companyContact;
    String companyName;
    String image;
    String gst;
    Long addressId;
    String addressLine;
    String city;
    String state;
    String country;
    String zipCode;
}
