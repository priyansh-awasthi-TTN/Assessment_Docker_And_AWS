package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    final UserRepository userRepository;
    final MessageService messageService;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findWithRolesByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_FOUND), 400));

        return new CustomUserDetails(user);
    }
}
