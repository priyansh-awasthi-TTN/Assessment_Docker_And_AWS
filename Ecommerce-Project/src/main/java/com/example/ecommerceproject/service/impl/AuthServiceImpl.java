package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.ForgotPasswordRequestDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.ResetPasswordRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.entity.*;
import com.example.ecommerceproject.enums.AddressType;
import com.example.ecommerceproject.enums.RoleEnums;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.*;
import com.example.ecommerceproject.service.AuthService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.util.JwtUtil;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AuthServiceImpl implements AuthService {
    static final int MAX_FAILED_ATTEMPTS = 3;

    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final UserRoleRepository userRoleRepository;
    final CustomerRepository customerRepository;
    final SellerRepository sellerRepository;
    final AddressRepository addressRepository;
    final ActivationTokenRepository activationTokenRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final AuthenticationManager authenticationManager;
    final JwtUtil jwtUtil;
    final RefreshTokenRepository refreshTokenRepository;
    final MessageService messageService;
    final UserSessionService service;

    @Override
    @Transactional
    public ApiResponseDTO register(RegisterRequestDTO dto) {
        validateCustomerRegistration(dto);
        User user = createUser(dto);
        assignRole(user, RoleEnums.ROLE_CUSTOMER);
        createCustomer(user, dto.getPhoneNumber());
        createActivationToken(user);
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_REGISTRATION_SUCCESS), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO registerSeller(SellerRegisterRequestDTO dto) {
        validateSellerRegistration(dto);
        User user = createUser(dto);
        assignRole(user, RoleEnums.ROLE_SELLER);
        createSeller(user, dto);
        saveAddress(user, dto.getAddress());
        emailService.sendSellerRegistrationEmail(user.getEmail());
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_SELLER_REGISTRATION_SUCCESS), 200);
    }

    @Override
    @Transactional(noRollbackFor = ApiException.class)
    public ApiResponseDTO activateAccount(String tokenValue) {
        ActivationToken token = activationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_INVALID_ACTIVATION_TOKEN, 400));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            User user = token.getUser();
            activationTokenRepository.delete(token);
            createActivationToken(user);
            throw new ApiException(MessageKeys.AUTH_ACTIVATION_EXPIRED, 400);
        }
        User user = token.getUser();
        user.setActive(true);
        activationTokenRepository.delete(token);
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_ACTIVATION_SUCCESS), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO resendActivationLink(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));
        if (user.isActive()) {
            throw new ApiException(MessageKeys.AUTH_ACCOUNT_ALREADY_ACTIVATED, 400);
        }
        activationTokenRepository.deleteByUser(user);
        createActivationToken(user);
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_RESEND_ACTIVATION_SUCCESS), 200);
    }

    private void validateCustomerRegistration(RegisterRequestDTO dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_EMAIL_EXISTS, 409);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }
    }

    private void validateSellerRegistration(SellerRegisterRequestDTO dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_EMAIL_EXISTS, 409);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }
        if (sellerRepository.existsByGstIgnoreCase(dto.getGst())) {
            throw new ApiException(MessageKeys.VALIDATION_GST_EXISTS, 409);
        }
        if (sellerRepository.existsByCompanyNameIgnoreCase(dto.getCompanyName())) {
            throw new ApiException(MessageKeys.VALIDATION_COMPANY_NAME_EXISTS, 409);
        }
        validateSellerAddressLabel(dto.getAddress().getLabel());
    }

    private void validateSellerAddressLabel(AddressType label) {
        if (label != AddressType.OFFICE) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_SELLER_ADDRESS_LABEL, 400);
        }
    }

    @Override
    @Transactional(noRollbackFor = {ApiException.class, AuthenticationException.class})
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail().toLowerCase(), dto.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User entity = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));
            entity.setInvalidAttemptCount(0);

            service.revokeAllRefreshTokens(entity);

            String accessToken = jwtUtil.generateToken(userDetails.getUserId(), userDetails.getUsername(),
                    userDetails.getAuthorities());
            String accessTokenJti = jwtUtil.extractJti(accessToken);

            service.storeAccessToken(accessTokenJti, entity);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(entity);
            String refreshId = UUID.randomUUID().toString();
            refreshToken.setTokenId(refreshId);
            refreshToken.setAccessTokenJti(accessTokenJti);
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);

            String refreshTokenValue = jwtUtil.generateRefreshToken(userDetails.getUserId(), userDetails.getUsername(),
                    refreshId);

            return new LoginResponseDTO(
                    accessToken,
                    refreshTokenValue,
                    userDetails.getAuthorities().stream().toList(),
                    userDetails.getUsername(),
                    messageService.get(MessageKeys.AUTH_LOGIN_SUCCESS));
        } catch (AuthenticationException e) {
            userRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).ifPresent(user -> {
                if (isProtectedAdmin(user)) {
                    return;
                }
                int newCount = (user.getInvalidAttemptCount() == null ? 0 : user.getInvalidAttemptCount()) + 1;
                user.setInvalidAttemptCount(newCount);
                if (newCount >= MAX_FAILED_ATTEMPTS) {
                    user.setLocked(true);
                    emailService.sendAccountLockedEmail(user.getEmail());
                    throw new ApiException(MessageKeys.AUTH_ACCOUNT_LOCKED, 400);
                }
            });
            throw new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400);
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO logout(String accessTokenValue, String refreshTokenValue) {
        boolean handled = false;

        if (accessTokenValue != null && !accessTokenValue.isBlank()) {
            if (jwtUtil.isTokenValid(accessTokenValue)) {
                String accessTokenJti = jwtUtil.extractJti(accessTokenValue);
                service.deleteAccessToken(accessTokenJti);
                handled = true;
            }
        }

        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            if (jwtUtil.isRefreshTokenValid(refreshTokenValue)) {
                String refreshId = jwtUtil.extractRefreshId(refreshTokenValue);
                if (refreshId != null) {
                    service.deleteRefreshToken(refreshId);
                    handled = true;
                }
            }
        }

        if (!handled) {
            throw new ApiException(MessageKeys.AUTH_TOKEN_REQUIRED, 401);
        }

        return new ApiResponseDTO(
                messageService.get(MessageKeys.AUTH_LOGOUT_SUCCESS),
                200);
    }

    @Override
        @Transactional
        public LoginResponseDTO refreshAccessToken(String refreshTokenValue) {

            if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
                throw new ApiException(MessageKeys.AUTH_TOKEN_REQUIRED, 401);
            }

            if (!jwtUtil.isRefreshTokenValid(refreshTokenValue)) {
                throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 401);
            }

            Claims claims = jwtUtil.extractAllClaims(refreshTokenValue);

            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();
            String refreshId = jwtUtil.extractRefreshId(refreshTokenValue);

            if (userId == null || email == null || refreshId == null) {
                throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 401);
            }

            RefreshToken existingToken = refreshTokenRepository.findByTokenId(refreshId)
                    .orElseThrow(() -> new ApiException(
                            MessageKeys.AUTH_REFRESH_TOKEN_REVOKED, 401));

            User user = userRepository.findWithRolesByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new ApiException(
                            MessageKeys.AUTH_USER_NOT_FOUND, 404));

            if (!email.equalsIgnoreCase(user.getEmail())) {
                throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 401);
            }

            service.deleteAccessToken(existingToken.getAccessTokenJti());

            CustomUserDetails userDetails = new CustomUserDetails(user);
            String newAccessToken = jwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    userDetails.getAuthorities());

            String newAccessTokenJti = jwtUtil.extractJti(newAccessToken);

            service.storeAccessToken(newAccessTokenJti, user);

            existingToken.setAccessTokenJti(newAccessTokenJti);

            return new LoginResponseDTO(
                    newAccessToken,
                    refreshTokenValue,
                    userDetails.getAuthorities().stream().toList(),
                    user.getEmail(),
                    messageService.get(MessageKeys.AUTH_REFRESH_SUCCESS));
        }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO requestPasswordReset(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));
        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_ACCOUNT_NOT_ACTIVATED, 400);
        }
        long pwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String token = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail(), pwdUpdatedAtMillis);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_PASSWORD_RESET_SENT), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }
        String token = dto.getToken();
        if (!jwtUtil.isPasswordResetTokenValid(token)) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }
        Claims claims = jwtUtil.extractAllClaims(token);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        Long tokenPwdUpdatedAt = claims.get("pwdUpdatedAt", Long.class);
        if (userId == null || email == null || tokenPwdUpdatedAt == null) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }
        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_ACCOUNT_NOT_ACTIVATED, 400);
        }

        long currentPwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (currentPwdUpdatedAtMillis != tokenPwdUpdatedAt.longValue()) {
            throw new ApiException(MessageKeys.VALIDATION_RESET_TOKEN_USED, 400);
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        emailService.sendPasswordChangedEmail(user.getEmail());
        return new ApiResponseDTO(messageService.get(MessageKeys.AUTH_PASSWORD_UPDATED), 200);
    }

    private User createUser(RegisterRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail().toLowerCase());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        user.setActive(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setDeleted(false);
        user.setInvalidAttemptCount(0);
        return userRepository.save(user);
    }

    private User createUser(SellerRegisterRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        user.setActive(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setDeleted(false);
        user.setInvalidAttemptCount(0);
        return userRepository.save(user);
    }

    private void assignRole(User user, RoleEnums roleEnum) {
        Role role = roleRepository.findByAuthority(roleEnum)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_ROLE_NOT_FOUND, 404));
        UserRole userRole = new UserRole(
                new UserRoleId(user.getId(), role.getId()),
                user,
                role);
        userRoleRepository.save(userRole);
    }

    private void createCustomer(User user, String contact) {
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setContact(contact);
        customerRepository.save(customer);
    }

    private void createSeller(User user, SellerRegisterRequestDTO dto) {
        Seller seller = new Seller();
        seller.setUser(user);
        seller.setCompanyName(dto.getCompanyName());
        seller.setCompanyContact(dto.getCompanyContact());
        seller.setGst(dto.getGst());
        seller.setApproved(false);
        sellerRepository.save(seller);
    }

    private void saveAddress(User user, AddressDTO dto) {
        long existingAddressCount = addressRepository.countByUser(user);
        if (existingAddressCount > 0) {
            throw new ApiException(MessageKeys.VALIDATION_SELLER_SINGLE_ADDRESS, 400);
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setLabel(dto.getLabel());

        addressRepository.save(address);
    }

    private void createActivationToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        ActivationToken token = new ActivationToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(3));
        token.setUsed(false);
        activationTokenRepository.save(token);
        emailService.sendActivationEmail(user.getEmail(), tokenValue);
    }
    private boolean isProtectedAdmin(User user) {
        return user != null && MessageKeys.PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail());
    }
}
