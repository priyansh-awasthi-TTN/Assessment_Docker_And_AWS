package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerceproject.entity.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>{
    boolean existsByGstIgnoreCase(String gst);
    boolean existsByCompanyNameIgnoreCase(String companyName);
    Page<Seller> findByUser_EmailContainingIgnoreCase(String email, Pageable pageable);

    Optional<Seller> findByUser_Id(Long id);
}
