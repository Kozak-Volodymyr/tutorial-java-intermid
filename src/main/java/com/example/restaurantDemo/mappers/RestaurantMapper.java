package com.example.restaurantDemo.mappers;

import com.example.restaurantDemo.domain.RestaurantCreateUpdateRequest;
import com.example.restaurantDemo.domain.dto.GeoPointDto;
import com.example.restaurantDemo.domain.dto.RestaurantCreateUpdateRequestDto;
import com.example.restaurantDemo.domain.dto.RestaurantDto;
import com.example.restaurantDemo.domain.dto.RestaurantSummaryDto;
import com.example.restaurantDemo.domain.entities.Restaurant;
import com.example.restaurantDemo.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {
    RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(RestaurantCreateUpdateRequestDto restaurantCreateUpdateRequest);

    @Mapping(source = "reviews",target="totalReviews",qualifiedByName = "populateTotalReviews")
    RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(source = "reviews",target="totalReviews",qualifiedByName = "populateTotalReviews")
    RestaurantSummaryDto toSummaryDto(Restaurant restaurant);

    @Named("populateTotalReviews")
    default Integer populateTotalReviews(List<Review> reviews){
        return reviews.size();
    }

    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoPointDto toGeoPointDto(GeoPoint geoPoint);
}
