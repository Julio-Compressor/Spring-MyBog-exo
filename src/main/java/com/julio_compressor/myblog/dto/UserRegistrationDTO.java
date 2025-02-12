package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.User;
import jakarta.validation.constraints.*;

import java.io.Serializable;

public record UserRegistrationDTO(
        @Email(message = "L'adresse email doit être valide")
        @NotBlank(message = "L'adresse email ne peut pas être vide")
        @NotEmpty(message = "L'adresse email ne peut pas être vide")
        String email,

        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        @NotEmpty(message = "Le mot de passe ne peut pas être vide")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String password
) implements Serializable {
    public static UserRegistrationDTO mapFromEntity(User user) {
        return new UserRegistrationDTO(
                user.getEmail(),
                user.getPassword()
        );
    }
}
