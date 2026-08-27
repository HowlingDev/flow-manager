package com.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class FileErrorsHandler {

    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<String> handleMinioUploadException(MinioUploadException e) {

        return ResponseEntity.internalServerError().body("Failed to upload file" + e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException() {
        return new ResponseEntity<>("файл не найден",HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MinioDownloadException.class)
    public ResponseEntity<String> handleMinioDownloadException(MinioDownloadException e) {
        return ResponseEntity.internalServerError().body("Failed to download file" + e.getMessage());
    }
}
