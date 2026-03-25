package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.entity.AccessToken;
import com.example.ecommerceproject.entity.RefreshToken;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.repository.AccessTokenRepository;
import com.example.ecommerceproject.repository.ActivationTokenRepository;
import com.example.ecommerceproject.repository.RefreshTokenRepository;
import com.example.ecommerceproject.service.UserSessionService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserSessionServiceImpl implements UserSessionService {

    final RefreshTokenRepository refreshTokenRepository;
    final AccessTokenRepository accessTokenRepository;
    final ActivationTokenRepository activationTokenRepository;

    @Override
    @Transactional
    public int revokeAllRefreshTokens(User user) {
        if (user == null) {
            return 0;
        }

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);

        if (!tokens.isEmpty()) {
            refreshTokenRepository.deleteAll(tokens);
        }

        accessTokenRepository.deleteByUser(user);

        activationTokenRepository.deleteByUser(user);

        return tokens.size();
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshId) {
        refreshTokenRepository.findByTokenId(refreshId)
                .ifPresent(token -> {
                    refreshTokenRepository.delete(token);
                    accessTokenRepository.deleteByUser(token.getUser());
                });
    }

    @Override
    @Transactional
    public void storeAccessToken(String jti, User user) {
        AccessToken accessToken = new AccessToken(jti, user);
        accessTokenRepository.save(accessToken);
    }

    @Override
    @Transactional
    public void deleteAccessToken(String jti) {
        accessTokenRepository.findByJti(jti)
                .ifPresent(accessTokenRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccessTokenValid(String jti) {
        return accessTokenRepository.existsByJti(jti);
    }
}
