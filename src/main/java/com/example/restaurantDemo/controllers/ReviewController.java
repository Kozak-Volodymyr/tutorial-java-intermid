package com.example.restaurantDemo.controllers;

import com.example.restaurantDemo.domain.ReviewCreateUpdateRequest;
import com.example.restaurantDemo.domain.dto.ReviewCreateUpdateRequestDto;
import com.example.restaurantDemo.domain.dto.ReviewDto;
import com.example.restaurantDemo.domain.entities.Review;
import com.example.restaurantDemo.domain.entities.User;
import com.example.restaurantDemo.mappers.ReviewMapper;
import com.example.restaurantDemo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable String restaurantId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto reviewDto,
            @AuthenticationPrincipal Jwt jwt){
       ReviewCreateUpdateRequest request= reviewMapper.toReviewCreateUpdateRequest(reviewDto);
       User author=jwtToUser(jwt);
       Review review=reviewService.createReview(author,restaurantId,request);
       return ResponseEntity.ok(reviewMapper.toReviewDto(review));
    }
    private User jwtToUser(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .givenName(jwt.getClaimAsString("given_name"))
                .username(jwt.getClaimAsString("preferred_username"))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }

    @GetMapping
    public Page<ReviewDto> listReviews(@PathVariable String restaurantId,
                                       @PageableDefault(size=20,page=0,
                                       sort = "datePosted",
                                       direction = Sort.Direction.DESC)
                                       Pageable pageable
                                       ){
        return reviewService
                .listReviews(restaurantId,pageable)
                .map(reviewMapper::toReviewDto);

    }
    @GetMapping(path="/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable String reviewId,
    @PathVariable String restaurantId){
        return reviewService.getReview(reviewId,restaurantId).map(reviewMapper::toReviewDto).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.noContent().build());
    }

    @PutMapping(path="/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable String reviewId,
                                                  @PathVariable String restaurantId,
    @Valid @RequestBody ReviewCreateUpdateRequestDto requestDto,
    @AuthenticationPrincipal Jwt jwt){
        ReviewCreateUpdateRequest request= reviewMapper.toReviewCreateUpdateRequest(requestDto);
        User author=jwtToUser(jwt);
        Review review=reviewService.updateReview(author,reviewId,restaurantId,request);
        return ResponseEntity.ok(reviewMapper.toReviewDto(review));

    }

    @DeleteMapping(path="/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId,
    @PathVariable String restaurantId){
        reviewService.deleteReview(reviewId,restaurantId);
        return ResponseEntity.noContent().build();
    }
}
