package com.example.restaurantDemo.domain.dto;

import com.example.restaurantDemo.domain.entities.Address;
import com.example.restaurantDemo.domain.entities.OperatingHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantCreateUpdateRequestDto {
    @NotBlank(message = "Restaurant name required")
    private String name;
    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;
    @NotBlank(message = "Contact info is required")
    private String contactInformation;
    @Valid
    private AddressDto address;
    @Valid
    private OperatingHoursDto operatingHours;
    @Size(min = 1,message = "At least 1 photo required")
    private List<String> photoIds;
}
