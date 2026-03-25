package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class VariationListDTO {
    Long id;
    Double price;
    Map<String, String> metadata;
    String primaryImageUrl;
}
