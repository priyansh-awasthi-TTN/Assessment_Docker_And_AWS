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
public class CategoryFilterDetailsDTO {
    List<CategoryMetadataDTO> metadataFields;
    List<String> brands;
    Double minPrice;
    Double maxPrice;
}
