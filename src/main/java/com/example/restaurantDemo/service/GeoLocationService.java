package com.example.restaurantDemo.service;

import com.example.restaurantDemo.domain.GeoLocation;
import com.example.restaurantDemo.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation geoLocate(Address address);
}
