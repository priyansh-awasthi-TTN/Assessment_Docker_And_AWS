package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ProductCreateRequest {
    @NotBlank(message = "{validation.name_required}")
    String name;

    @NotBlank(message = "{validation.brand_required}")
    String brand;

    @NotNull(message = "{category.category_id_required}")
    Long categoryId;

    String desc;
    Boolean isCancellable = false;
    Boolean isReturnable = false;
}
