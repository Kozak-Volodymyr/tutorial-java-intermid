package com.example.restaurantDemo.domain.dto;

import com.example.restaurantDemo.domain.entities.TimeRange;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingHoursDto {
    @Valid
    private TimeRangeDto monday;
    @Valid
    private TimeRangeDto tuesday;
    @Valid
    private TimeRangeDto wednesday;
    @Valid
    private TimeRangeDto thursday;
    @Valid
    private TimeRangeDto friday;
    @Valid
    private TimeRangeDto saturday;
    @Valid
    private TimeRangeDto sunday;
}
