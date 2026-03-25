package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {
    Long id;
    String name;
    Long parentCategoryId;

    public CategoryResponseDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
