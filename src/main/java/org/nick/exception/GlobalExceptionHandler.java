package org.nick.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleImageUploadException(ImageUploadException imageUploadException) {
        return ResponseEntity.status(500).body(imageUploadException.getMessage());
    }
}
