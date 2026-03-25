package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerceproject.entity.Role;
import java.util.Optional;
import com.example.ecommerceproject.enums.RoleEnums;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByAuthority(RoleEnums authority);
    boolean existsByAuthority(RoleEnums authority);
}
