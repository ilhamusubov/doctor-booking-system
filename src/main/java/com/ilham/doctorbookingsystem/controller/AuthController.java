package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.LoginRequest;
import com.ilham.doctorbookingsystem.model.request.RegisterDoctorRequest;
import com.ilham.doctorbookingsystem.model.request.RegisterPatientRequest;
import com.ilham.doctorbookingsystem.model.request.VerifyOtpRequestDto;
import com.ilham.doctorbookingsystem.model.response.AuthResponse;
import com.ilham.doctorbookingsystem.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/patient")
    public String registerPatient(@RequestBody @Valid RegisterPatientRequest request) {
        return authenticationService.registerPatient(request);
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@RequestBody @Valid RegisterDoctorRequest request) {
        return authenticationService.registerDoctor(request);
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@RequestBody @Valid VerifyOtpRequestDto request) {
        return authenticationService.verifyOtp(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestParam String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        return authenticationService.logout(authHeader);
    }
}
