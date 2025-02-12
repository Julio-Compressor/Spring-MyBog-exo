package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.UserLoginDTO;
import com.julio_compressor.myblog.dto.UserRegistrationDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.model.User;
import com.julio_compressor.myblog.security.AuthenticationService;
import com.julio_compressor.myblog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        User registeredUser = userService.registerUser(
                userRegistrationDTO.email(),
                userRegistrationDTO.password(),
                Set.of("ROLE_USER") // Par défaut, chaque utilisateur aura le rôle "USER"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody UserLoginDTO userLoginDTO) {
        String token = authenticationService.authenticate(userLoginDTO.email(), userLoginDTO.password());
        return ResponseEntity.ok(token);
    }
}
