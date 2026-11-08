package com.example.restaurantDemo.controllers;

import com.example.restaurantDemo.domain.dto.ErrorDto;
import com.example.restaurantDemo.exceptions.BaseException;
import com.example.restaurantDemo.exceptions.RestaurantNotFoundException;
import com.example.restaurantDemo.exceptions.ReviewNotAllowedException;
import com.example.restaurantDemo.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ErrorDto> handleReviewNotAllowedException(ReviewNotAllowedException ex) {
        log.error("Caught ReviewNotAllowedException",ex.getMessage());
        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Review not can be created or changed")
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorDto> handleRestaurantNotFoundException(RestaurantNotFoundException e) {
        log.error("Caught RestaurantNotFoundException",e.getMessage());
        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Restaurant Not Found")
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Caught MethodArgumentNotValidException",ex);
        String errorMessage=ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error->error.getField()+":"+error.getDefaultMessage()).collect(Collectors.joining(", "));
        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorDto> handleStorageException(StorageException ex) {
        log.error("Caught StorageException",ex);

        ErrorDto errorDto= ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unable to save or retrieve file")
                .build();

        return new ResponseEntity<>(errorDto,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDto> handleBaseException(BaseException ex) {
        log.error("Caught BaseException", ex);

        ErrorDto error = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Catch-all for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        log.error("Caught unexpected exception", ex);

        ErrorDto error = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
