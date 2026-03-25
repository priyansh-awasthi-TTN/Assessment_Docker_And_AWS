package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class LoginResponseDTO {
    String accessToken;
    String refreshToken;
    List<? extends GrantedAuthority> role;
    String email;
    String message;
}
