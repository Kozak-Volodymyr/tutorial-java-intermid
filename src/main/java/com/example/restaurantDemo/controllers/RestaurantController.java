package com.example.restaurantDemo.controllers;

import com.example.restaurantDemo.domain.RestaurantCreateUpdateRequest;
import com.example.restaurantDemo.domain.dto.RestaurantCreateUpdateRequestDto;
import com.example.restaurantDemo.domain.dto.RestaurantDto;
import com.example.restaurantDemo.domain.dto.RestaurantSummaryDto;
import com.example.restaurantDemo.domain.entities.Restaurant;
import com.example.restaurantDemo.mappers.RestaurantMapper;
import com.example.restaurantDemo.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;


    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDto request) {
        RestaurantCreateUpdateRequest restaurantCreateUpdateRequest = restaurantMapper.toRestaurantCreateUpdateRequest(request);
        Restaurant restaurant = restaurantService.createRestaurant(restaurantCreateUpdateRequest);
        return ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant));
    }

    @GetMapping
    public Page<RestaurantSummaryDto> searchRestaurants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float longitude,
            @RequestParam(required = false) Float latitude,
            @RequestParam(required = false) Float radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<Restaurant> searchResults = restaurantService.searchRestaurants(q, minRating, latitude, longitude,
                radius, PageRequest.of(page, size));

        return searchResults.map(restaurantMapper::toSummaryDto);
    }

    @GetMapping(path = "/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable String restaurantId) {
        return restaurantService.getRestaurant(restaurantId).map(restaurant -> ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant))).orElse(ResponseEntity.notFound().build());

    }


    @PutMapping(path = "/{restaurantId}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable String restaurantId, @Valid @RequestBody RestaurantCreateUpdateRequestDto request) {
        RestaurantCreateUpdateRequest restaurantCreateUpdateRequest = restaurantMapper.toRestaurantCreateUpdateRequest(request);
        Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, restaurantCreateUpdateRequest);
        return new ResponseEntity<>(restaurantMapper.toRestaurantDto(restaurant), HttpStatus.OK);
    }

    @DeleteMapping(path="{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
