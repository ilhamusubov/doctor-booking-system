package com.ilham.doctorbookingsystem.mapper;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class})
public interface AppointmentMapper {

    AppointmentResponseDto entityToDto(AppointmentEntity appointmentEntity);
}
