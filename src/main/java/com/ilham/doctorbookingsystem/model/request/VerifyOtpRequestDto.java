package com.ilham.doctorbookingsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String otp;
}
