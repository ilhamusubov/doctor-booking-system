package com.ilham.doctorbookingsystem.mapper;

import com.ilham.doctorbookingsystem.entity.ReviewEntity;
import com.ilham.doctorbookingsystem.model.response.ReviewResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponseDto entityToDto(ReviewEntity reviewEntity);
}
