package com.ilham.doctorbookingsystem.mapper;

import com.ilham.doctorbookingsystem.entity.ReviewEntity;
import com.ilham.doctorbookingsystem.model.response.DoctorReviewResponseDto;
import com.ilham.doctorbookingsystem.model.response.ReviewResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponseDto entityToDto(ReviewEntity reviewEntity);

    @Mapping(source = "patient.user.firstName", target = "patientName")
    DoctorReviewResponseDto entityToForDto(ReviewEntity reviewEntity);
}
