package com.example.restaurantDemo.mappers;

import com.example.restaurantDemo.domain.ReviewCreateUpdateRequest;
import com.example.restaurantDemo.domain.dto.ReviewCreateUpdateRequestDto;
import com.example.restaurantDemo.domain.dto.ReviewDto;
import com.example.restaurantDemo.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateRequestDto reviewDto);

    ReviewDto toReviewDto(Review review);
}
