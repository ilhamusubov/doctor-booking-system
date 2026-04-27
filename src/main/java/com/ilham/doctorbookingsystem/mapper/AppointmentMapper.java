package com.ilham.doctorbookingsystem.mapper;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class})
public interface AppointmentMapper {

    AppointmentResponseDto entityToDto(AppointmentEntity appointmentEntity);

}
