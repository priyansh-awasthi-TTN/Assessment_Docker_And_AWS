package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ProductVariationCreateRequest {

    @NotEmpty(message = "{validation.metadata_is_mandatory}")
    Map<String, String> metadata;

    @NotNull(message = "{validation.quantity_required}")
    @Min(value = 0, message = "{validation.quantity_cannot_be_less_than_zero}")
    Long quantityAvailable;

    @Min(value = 0, message = "{validation.price_cannot_be_less_than_zero}")
    @NotNull(message = "{validation.price_required}")
    Double price;
}
