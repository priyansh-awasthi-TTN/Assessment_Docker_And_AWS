package com.example.ecommerceproject.service;

import com.example.ecommerceproject.entity.User;

public interface UserSessionService {
    int revokeAllRefreshTokens(User user);

    void deleteRefreshToken(String refreshId);

    void storeAccessToken(String jti, User user);

    void deleteAccessToken(String jti);

    boolean isAccessTokenValid(String jti);
}
