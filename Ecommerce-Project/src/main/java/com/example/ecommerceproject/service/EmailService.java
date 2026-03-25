package com.example.ecommerceproject.service;

public interface EmailService {
    void sendActivationEmail(String toEmail, String token);
    void sendSellerRegistrationEmail(String email);
    void sendAccountLockedEmail(String email);
    void sendPasswordResetEmail(String toEmail, String token);
    void sendPasswordChangedEmail(String toEmail);
    void sendAccountActivationEmail(String email);
    void sendAccountDeactivationEmail(String email);
    void sendProductStatusEmail(String sellerEmail, String productName, boolean isActivated);
    void sendProductCreatedNotificationToAdmin(String sellerName, String sellerEmail, String productName, String categoryName, String brand);
}
