package com.example.restaurantDemo.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeRangeDto {
    @NotBlank(message = "Must have open hours")
    @Pattern(regexp="^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    private String openTime;
    @Pattern(regexp="^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    @NotBlank(message = "Must have close hours")
    private String closeTime;
}
