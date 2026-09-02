package com.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileErrorsHandler {

    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<String> handleMinioUploadException(MinioUploadException e) {

        return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleNoSuchElementException(FileNotFoundException e) {

        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MinioDownloadException.class)
    public ResponseEntity<String> handleMinioDownloadException(MinioDownloadException e) {

        return ResponseEntity.internalServerError().body("Failed to download file: " + e.getMessage());
    }

    @ExceptionHandler(DownloadStatusException.class)
    public ResponseEntity<String> handleDownloadStatusException(DownloadStatusException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SubscriptionStatusException.class)
    public ResponseEntity<String> handleSubscriptionStatusException(SubscriptionStatusException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
