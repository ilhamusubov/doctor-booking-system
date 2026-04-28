package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;


@Data
public class PatientResponseDto {
    private Long id;
    private UserResponseDto user;
}
