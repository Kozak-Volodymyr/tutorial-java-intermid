package com.example.restaurantDemo.service;

import com.example.restaurantDemo.domain.ReviewCreateUpdateRequest;
import com.example.restaurantDemo.domain.entities.Review;
import com.example.restaurantDemo.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {
    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);

    Page<Review> listReviews(String restaurantId, Pageable pageable);
    Optional<Review> getReview(String reviewId,String restaurantId);
    Review updateReview(User author, String reviewId, String restaurantId, ReviewCreateUpdateRequest request);
    void deleteReview(String reviewId, String restaurantId);
}
