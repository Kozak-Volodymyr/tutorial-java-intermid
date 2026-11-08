package com.example.restaurantDemo.domain.dto;

import com.example.restaurantDemo.domain.entities.Photo;
import com.example.restaurantDemo.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    private String id;

    private String content;

    private Integer rating;

    private LocalDateTime posted;

    private LocalDateTime lastEdited;

    private List<PhotoDto> photos=new ArrayList<>();

    private UserDto writtenBy;
}
