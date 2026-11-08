package com.example.restaurantDemo.service.impl;

import com.example.restaurantDemo.domain.GeoLocation;
import com.example.restaurantDemo.domain.RestaurantCreateUpdateRequest;
import com.example.restaurantDemo.domain.entities.Address;
import com.example.restaurantDemo.domain.entities.Photo;
import com.example.restaurantDemo.domain.entities.Restaurant;
import com.example.restaurantDemo.exceptions.RestaurantNotFoundException;
import com.example.restaurantDemo.repositories.RestaurantRepository;
import com.example.restaurantDemo.service.GeoLocationService;
import com.example.restaurantDemo.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;
    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {
        Address address = request.getAddress();
        GeoLocation geoLocation=geoLocationService.geoLocate(address);
        GeoPoint geoPoint=new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());

       List<String> photoIds=request.getPhotoIds();
       List<Photo> photos=photoIds.stream().map(photoUrl-> Photo.builder()
               .url(photoUrl)
               .uploadDate(LocalDateTime.now())
               .build()).toList();
       Restaurant restaurant=Restaurant.builder()
               .name(request.getName())
               .address(address)
               .photos(photos)
               .contactInformation(request.getContactInformation())
               .cuisineType(request.getCuisineType())
               .geoLocation(geoPoint)
               .operatingHours(request.getOperatingHours())
               .averageRating(0f)
               .build();

       return restaurantRepository.save(restaurant);

    }

    @Override
    public Page<Restaurant> searchRestaurants(String query, Float minRating, Float latitude, Float longitude,Float radius, Pageable pageable) {
        if(null!=minRating &&(null==query || query.isEmpty())){
            return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating,pageable);
        }
        Float searchMinRating=(null==minRating) ? 0f : minRating;
        if(null!=query && !query.trim().isEmpty()){
            return restaurantRepository.findByQueryAndMinRating(query,searchMinRating,pageable);
        }

        if(null!=latitude && null!=longitude && null!=radius){
            return restaurantRepository.findByLocationNear(latitude,longitude,radius,pageable);
        }
        return restaurantRepository.findAll(pageable);
    }

    @Override
    public Optional<Restaurant> getRestaurant(String id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest request) {
        Restaurant restaurant=getRestaurant(id).orElseThrow(()->new RestaurantNotFoundException("Restaurant Not Found:"+id));
        GeoLocation geoLocation=geoLocationService.geoLocate(restaurant.getAddress());
        GeoPoint geoPoint=new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
        List<String> photoIds=request.getPhotoIds();
        List<Photo> photos=photoIds.stream().map(photoUrl->Photo.builder()
                .url(photoUrl)
                .uploadDate(LocalDateTime.now())
                .build()).toList();

        restaurant.setName(request.getName());
        restaurant.setAddress(restaurant.getAddress());
        restaurant.setPhotos(photos);
        restaurant.setContactInformation(request.getContactInformation());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setGeoLocation(geoPoint);
        restaurant.setOperatingHours(request.getOperatingHours());

        return restaurantRepository.save(restaurant);

    }

    @Override
    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }
}
