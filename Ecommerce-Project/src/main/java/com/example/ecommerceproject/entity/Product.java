package com.example.ecommerceproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.ecommerceproject.audit.Auditable;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Product extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 50)
    String name;

    @Column(name = "description", length = 100)
    String desc;

    @Column(name = "is_cancellable")
    Boolean isCancellable;

    @Column(name = "is_returnable")
    Boolean isReturnable;

    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;

    String brand;

    @ManyToOne
    @JoinColumn(name = "seller_user_id")
    Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product")
    List<ProductVariations> productVariationList;

    @OneToMany(mappedBy = "product")
    List<ProductReview> productReviews;
}
