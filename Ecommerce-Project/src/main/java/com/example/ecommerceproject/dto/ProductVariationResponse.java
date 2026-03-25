package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ProductVariationResponse {
    Long id;
    Long productId;
    Long quantityAvailable;
    Double price;
    Map<String, String> metadata;
    String primaryImageUrl;
    List<String> secondaryImageUrls;
    Boolean isActive;
}
