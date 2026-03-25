package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = PRIVATE)
public class EmailServiceImpl implements EmailService {

    final String from;
    final JavaMailSender mailSender;
    final MessageService messageService;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from,
            MessageService messageService) {
        this.mailSender = mailSender;
        this.from = from;
        this.messageService = messageService;
    }

    @Override
    @Async
    public void sendActivationEmail(String toEmail, String token) {
        String activationLink = "http:localhost:8080/activate?token="+token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(messageService.get(MessageKeys.EMAIL_ACTIVATION_SUBJECT));
        mailMessage.setText(messageService.get(MessageKeys.EMAIL_ACTIVATION_BODY, activationLink));
        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendSellerRegistrationEmail(String email) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(email);
        mail.setSubject(messageService.get(MessageKeys.EMAIL_SELLER_REGISTRATION_SUBJECT));
        mail.setText(messageService.get(MessageKeys.EMAIL_SELLER_REGISTRATION_BODY));

        mailSender.send(mail);
    }

    @Override
    @Async
    public void sendAccountLockedEmail(String email) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(email);
        mail.setSubject(messageService.get(MessageKeys.EMAIL_ACCOUNT_LOCKED_SUBJECT));
        mail.setText(messageService.get(MessageKeys.EMAIL_ACCOUNT_LOCKED_BODY));
        mailSender.send(mail);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http:localhost:8080/password-reset?token="+token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(messageService.get(MessageKeys.EMAIL_PASSWORD_RESET_SUBJECT));
        mailMessage.setText(messageService.get(MessageKeys.EMAIL_PASSWORD_RESET_BODY, resetLink));
        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendPasswordChangedEmail(String toEmail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(messageService.get(MessageKeys.EMAIL_PASSWORD_CHANGED_SUBJECT));
        mailMessage.setText(messageService.get(MessageKeys.EMAIL_PASSWORD_CHANGED_BODY));
        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendAccountActivationEmail(String toEmail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(messageService.get(MessageKeys.EMAIL_ACCOUNT_ACTIVATED_SUBJECT));
            message.setText(messageService.get(MessageKeys.EMAIL_ACCOUNT_ACTIVATED_BODY));
            mailSender.send(message);
    }

    @Override
    @Async
    public void sendAccountDeactivationEmail(String toEmail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(messageService.get(MessageKeys.EMAIL_ACCOUNT_DEACTIVATED_SUBJECT));
            message.setText(messageService.get(MessageKeys.EMAIL_ACCOUNT_DEACTIVATED_BODY));
            mailSender.send(message);
    }

    @Override
    @Async
    public void sendProductStatusEmail(String sellerEmail, String productName, boolean isActivated) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sellerEmail);

        if (isActivated) {
            message.setSubject(messageService.get(MessageKeys.EMAIL_PRODUCT_ACTIVATED_SUBJECT));
            message.setText(messageService.get(MessageKeys.EMAIL_PRODUCT_ACTIVATED_BODY, productName));
        } else {
            message.setSubject(messageService.get(MessageKeys.EMAIL_PRODUCT_DEACTIVATED_SUBJECT));
            message.setText(messageService.get(MessageKeys.EMAIL_PRODUCT_DEACTIVATED_BODY, productName));
        }

        mailSender.send(message);
    }

    @Override
    @Async
    public void sendProductCreatedNotificationToAdmin(String sellerName, String sellerEmail, String productName, String categoryName, String brand) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(MessageKeys.PROTECTED_ADMIN_EMAIL);
        message.setSubject(messageService.get(MessageKeys.EMAIL_PRODUCT_CREATED_SUBJECT));
        message.setText(messageService.get(MessageKeys.EMAIL_PRODUCT_CREATED_BODY,
            sellerName, sellerEmail, productName, categoryName, brand));

        mailSender.send(message);
    }
}
