package com.example.ecommerceproject.entity;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="activation_token")
@FieldDefaults(level = PRIVATE)
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique=true, nullable=false, length=64)
    String token;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", nullable=false)
    User user;

    @Column(nullable=false)
    LocalDateTime expiryDate;

    boolean used=false;
}
