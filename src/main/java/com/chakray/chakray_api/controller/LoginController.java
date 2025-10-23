package com.chakray.chakray_api.controller;

import com.chakray.chakray_api.model.User;
import com.chakray.chakray_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Record para encapsular la solicitud de login
record LoginRequest(String taxId, String password) {
}

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // POST /login
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userService.authenticate(request.taxId(), request.password());

        return userOptional
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid tax_id or password."));
    }
}
