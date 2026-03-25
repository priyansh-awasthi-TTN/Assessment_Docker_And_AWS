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
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "token_id", nullable = false, unique = true, length = 100)
    String tokenId;

    @Column(name = "access_token_jti", nullable = false, length = 100)
    String accessTokenJti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "revoked", nullable = false)
    boolean revoked = false;

    public RefreshToken(String tokenId, String accessTokenJti, User user) {
        this.tokenId = tokenId;
        this.accessTokenJti = accessTokenJti;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.revoked = false;
    }
}
