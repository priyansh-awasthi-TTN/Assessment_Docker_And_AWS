package com.example.ecommerceproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import static lombok.AccessLevel.PRIVATE;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.ecommerceproject.audit.Auditable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@SQLDelete(sql = "UPDATE customer SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Customer extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="user_id", unique=true)
    User user;

    @Column(nullable = false, length = 10)
    String contact;

    @Column(name = "is_deleted")
    boolean isDeleted;
}
