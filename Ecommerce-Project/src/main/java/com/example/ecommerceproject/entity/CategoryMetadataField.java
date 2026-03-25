package com.example.ecommerceproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Category_Metadata_Field")
public class CategoryMetadataField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 40)
    String name;

    @OneToMany(
            mappedBy = "metadataField",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<CategoryMetadataFieldValues> fieldValues;
}
