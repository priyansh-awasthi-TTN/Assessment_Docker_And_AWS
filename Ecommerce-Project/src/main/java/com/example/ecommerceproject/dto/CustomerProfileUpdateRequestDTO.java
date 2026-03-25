package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level =  PRIVATE)
public class CustomerProfileUpdateRequestDTO {
    String firstName;
    String lastName;
    String contact;
}
