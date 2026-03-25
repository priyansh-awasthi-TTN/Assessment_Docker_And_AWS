package com.example.ecommerceproject.entity;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "access_tokens")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "jti", nullable = false, unique = true, length = 100)
    String jti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    public AccessToken(String jti, User user) {
        this.jti = jti;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
