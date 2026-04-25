package com.ilham.doctorbookingsystem.mapper;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.model.response.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorResponseDto entityToResponse(DoctorEntity doctorEntity);

    UserResponseDto entityToResponse(UserEntity userEntity);
}
