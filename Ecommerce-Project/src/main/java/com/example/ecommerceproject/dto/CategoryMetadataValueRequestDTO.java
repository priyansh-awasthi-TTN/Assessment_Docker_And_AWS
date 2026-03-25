package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CategoryMetadataValueRequestDTO {
    @NotNull(message = "{category.invalid_metadata_field_id}")
    Long metaDataFieldId;

    @NotNull(message = "{category.metadata_field_values_required}")
    @Size(min = 1, message = "{category.metadata_field_values_required}")
    List<String> values;
}
