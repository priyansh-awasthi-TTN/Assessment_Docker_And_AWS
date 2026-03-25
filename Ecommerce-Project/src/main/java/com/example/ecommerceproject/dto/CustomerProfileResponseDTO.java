package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerProfileResponseDTO {
    Long id;
    String firstName;
    String lastName;
    boolean isActive;
    String contact;
    String image;
}
