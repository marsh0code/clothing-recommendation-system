package com.example.wardrobeapi.web;

import com.example.wardrobeapi.exception.*;
import com.example.wardrobeapi.web.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class WardrobeApiExceptionHandler {

    @ExceptionHandler({
            ImageUploadException.class,
            HttpMessageNotReadableException.class,
            UserAlreadyExistsException.class,
            MaxUploadSizeExceededException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ClothingItemNotFound.class,
            ImageNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exceptionResponse(exception.getMessage()));
    }

    private ExceptionResponse exceptionResponse(String message) {
        return new ExceptionResponse(message,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
    }
}
