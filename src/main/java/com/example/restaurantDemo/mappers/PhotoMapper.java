package com.example.restaurantDemo.mappers;

import com.example.restaurantDemo.domain.dto.PhotoDto;
import com.example.restaurantDemo.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {
    PhotoDto toDto(Photo photo);

}
