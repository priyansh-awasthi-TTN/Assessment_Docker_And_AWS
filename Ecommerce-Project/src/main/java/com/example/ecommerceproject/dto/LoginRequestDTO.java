package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class LoginRequestDTO {

    @NotBlank(message = "{validation.email_required}")
    @Email(message = "{validation.email_invalid}")
    String email;

    @NotBlank(message = "{validation.password_required}")
    String password;
}
