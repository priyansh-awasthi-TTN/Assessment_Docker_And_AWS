package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerceproject.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    @EntityGraph(attributePaths = { "userRoles", "userRoles.role" })
    Optional<User> findWithRolesByEmailAndIsDeletedFalse(String email);
}
