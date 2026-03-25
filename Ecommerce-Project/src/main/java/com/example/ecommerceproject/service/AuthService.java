package com.example.ecommerceproject.service;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.ForgotPasswordRequestDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.ResetPasswordRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;

public interface AuthService {
    ApiResponseDTO register(RegisterRequestDTO registerRequestDTO);
    ApiResponseDTO activateAccount(String tokenValue);
    ApiResponseDTO resendActivationLink(String email);
    ApiResponseDTO registerSeller(SellerRegisterRequestDTO registerRequestDTO);
    LoginResponseDTO login(LoginRequestDTO responseDTO);
    ApiResponseDTO logout(String accessToken, String refreshToken);
    LoginResponseDTO refreshAccessToken(String refreshToken);
    ApiResponseDTO requestPasswordReset(ForgotPasswordRequestDTO dto);
    ApiResponseDTO resetPassword(ResetPasswordRequestDTO dto);
}
