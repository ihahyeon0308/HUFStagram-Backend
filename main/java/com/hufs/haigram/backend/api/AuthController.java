package com.hufs.haigram.backend.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hufs.haigram.backend.service.DemoSocialService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final DemoSocialService socialService;

    public AuthController(DemoSocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/login")
    public ApiModels.AuthResponse login(@Valid @RequestBody ApiModels.LoginRequest request) {
        return socialService.login(request);
    }

    @PostMapping("/signup")
    public ApiModels.AuthResponse signup(@Valid @RequestBody ApiModels.SignupRequest request) {
        return socialService.signup(request);
    }
}
