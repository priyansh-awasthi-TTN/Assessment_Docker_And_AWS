package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.ActivationToken;
import com.example.ecommerceproject.entity.User;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long>{
    Optional<ActivationToken> findByToken(String token);
    void deleteByUser(User user);
}
