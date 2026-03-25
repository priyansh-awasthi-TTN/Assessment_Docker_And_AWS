package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerceproject.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Page<Customer> findByUser_EmailContainingIgnoreCase(String email, Pageable pageable);
    Optional<Customer> findByUserId(Long userId);
}
