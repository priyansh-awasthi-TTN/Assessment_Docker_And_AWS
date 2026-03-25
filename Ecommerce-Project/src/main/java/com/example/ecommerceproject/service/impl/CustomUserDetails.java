package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ecommerceproject.entity.User;

import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE)
public class CustomUserDetails implements UserDetails {

    final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoles()
                .stream().map(ur -> new SimpleGrantedAuthority(ur.getRole().getAuthority().name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        LocalDateTime updated = user.getPasswordUpdateDate();

        return updated == null ||
                updated.plusDays(90).isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();

    }

}
