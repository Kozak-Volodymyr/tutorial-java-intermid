package com.example.restaurantDemo.service.impl;

import com.example.restaurantDemo.domain.ReviewCreateUpdateRequest;
import com.example.restaurantDemo.domain.entities.Photo;
import com.example.restaurantDemo.domain.entities.Restaurant;
import com.example.restaurantDemo.domain.entities.Review;
import com.example.restaurantDemo.domain.entities.User;
import com.example.restaurantDemo.exceptions.RestaurantNotFoundException;
import com.example.restaurantDemo.exceptions.ReviewNotAllowedException;
import com.example.restaurantDemo.repositories.RestaurantRepository;
import com.example.restaurantDemo.service.ReviewService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final RestaurantRepository restaurantRepository;
    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExistedReview=restaurant.getReviews().stream().anyMatch(r->r.getWrittenBy().getId().equals(author.getId()));

        if(hasExistedReview){
            throw new ReviewNotAllowedException("Review already exists");
        }
        LocalDateTime now=LocalDateTime.now();
        List<Photo> photos=review.getPhotoIds().stream().map(url->{
            return Photo.builder()
                    .url(url)
                    .uploadDate(now)
                    .build();

        }).toList();
        String reviewId=UUID.randomUUID().toString();

        Review reviewToCreate = Review.builder()
                .id(reviewId)
                .content(review.getContent())
                .rating(review.getRating())
                .photos(photos)
                .posted(now)
                .lastEdited(now)
                .writtenBy(author)
                .build();
        restaurant.getReviews().add(reviewToCreate);
        updateRestaurantAverageRating(restaurant);
        Restaurant savedRestaurant=restaurantRepository.save(restaurant);
        return savedRestaurant.getReviews().stream().filter(r->reviewId.equals(r.getId())).findFirst().orElseThrow(()->new RuntimeException("Review not found"));

    }

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant=restaurantRepository.findById(restaurantId).orElseThrow(()->new RestaurantNotFoundException(restaurantId));
        List<Review> reviews=restaurant.getReviews();

        Sort sort=pageable.getSort();
        if(sort.isSorted()){
           Sort.Order order=sort.iterator().next();
           String property=order.getProperty();
           boolean isAsc=order.getDirection().isAscending();
        Comparator<Review> comparator=switch (property){
            case "datePosted"-> Comparator.comparing(Review::getPosted);
            case "rating"-> Comparator.comparing(Review::getRating);
            default -> Comparator.comparing(Review::getPosted);
        };
        reviews.sort(isAsc?comparator:comparator.reversed());
        }else {
            reviews.sort(Comparator.comparing(Review::getPosted));
        }
       int start=(int)pageable.getOffset();
        if(start>=reviews.size()){
            return new PageImpl<>(Collections.emptyList(),pageable,reviews.size());
        }

        int end=Math.min(start+pageable.getPageSize(),reviews.size());
        return new PageImpl<>(reviews.subList(start,end),pageable,reviews.size());
    }

    @Override
    public Optional<Review> getReview(String reviewId, String restaurantId) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);
        return getReviewFromRestaurant(reviewId, restaurant);

    }

    @Nonnull
    private static Optional<Review> getReviewFromRestaurant(String reviewId, Restaurant restaurant) {
        return restaurant.getReviews().stream().filter(r -> r.getId().equals(reviewId)).findFirst();
    }

    @Override
    public Review updateReview(User author, String reviewId, String restaurantId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);
        String authorId=author.getId();
        Review existingReview=getReviewFromRestaurant(reviewId, restaurant).orElseThrow(()->new ReviewNotAllowedException("Review not allowed"));
        if(!authorId.equals(existingReview.getWrittenBy().getId())){
            throw new ReviewNotAllowedException("Cannot update another users review");
        }
        if(LocalDateTime.now().isAfter(existingReview.getPosted().plusHours(48))){
            throw new ReviewNotAllowedException("Cannot no longer can be edited");
        }
        existingReview.setContent(request.getContent());
       existingReview.setRating(request.getRating());
       existingReview.setLastEdited(LocalDateTime.now());
       existingReview.setWrittenBy(author);
       existingReview.setPhotos(request.getPhotoIds().stream().map(photoUrl->Photo.builder()
               .url(photoUrl)
               .uploadDate(LocalDateTime.now())
               .build()).toList());

       List<Review> updatedReviews=restaurant.getReviews().stream().filter(r->!reviewId.equals(r.getId())).collect(Collectors.toList());
        updatedReviews.add(existingReview);
        restaurant.setReviews(updatedReviews);
        updateRestaurantAverageRating(restaurant);
        restaurantRepository.save(restaurant);

        return existingReview;
    }

    @Override
    public void deleteReview(String reviewId, String restaurantId) {
        Restaurant restaurant=getRestaurantOrThrow(restaurantId);
        List<Review> reviews=restaurant.getReviews().stream().filter(r->!r.getId().equals(reviewId)).collect(Collectors.toList());
        restaurant.setReviews(reviews);
        updateRestaurantAverageRating(restaurant);
        restaurantRepository.save(restaurant);
    }

    @Nonnull
    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found:" + restaurantId));
    }

    private void updateRestaurantAverageRating(Restaurant restaurant){
        List<Review> reviews=restaurant.getReviews();
        if(reviews.isEmpty()){
            restaurant.setAverageRating(0.0f);
        } else {
            double averageRating=reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0f);
            restaurant.setAverageRating((float)averageRating);
        }
    }
}
