package com.ilham.doctorbookingsystem.model.response;

import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponseDto {
    private Long id;
    private String specialization;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String licenseNumber;
    private ApprovalStatus status;
    private UserResponseDto user;
}
