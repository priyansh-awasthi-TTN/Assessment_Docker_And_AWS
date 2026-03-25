package com.example.ecommerceproject.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.ecommerceproject.util.MessageKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UnifiedSecurityErrorHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        writeErrorResponse(response, HttpStatus.FORBIDDEN, MessageKeys.AUTH_ACCESS_DENIED);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (token.isBlank()) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token is empty");
                    return;
                }
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                return;
            } catch (Exception e) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
                return;
            }
        }
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, MessageKeys.AUTH_AUTHENTICATION_REQUIRED);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String messageKey)
            throws IOException {

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(messageKey, null, locale);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String jsonResponse = String.format(
                "{\"timestamp\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                timestamp, message, status.value());

        response.getWriter().write(jsonResponse);
    }
}
