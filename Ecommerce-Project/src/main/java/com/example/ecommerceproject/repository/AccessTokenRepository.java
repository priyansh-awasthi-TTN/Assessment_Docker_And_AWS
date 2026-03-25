package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.AccessToken;
import com.example.ecommerceproject.entity.User;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findByJti(String jti);

    boolean existsByJti(String jti);

    void deleteByUser(User user);
}
