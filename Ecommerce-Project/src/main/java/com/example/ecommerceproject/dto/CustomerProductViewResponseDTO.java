package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerProductViewResponseDTO {
    Long id;
    String name;
    String brand;
    String description;
    Boolean isCancellable;
    Boolean isReturnable;
    CategoryResponseDTO category;
    List<VariationDetailsDTO> variations;
}
