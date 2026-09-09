package com.example.exceptions;

import com.example.dto.ResponseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileErrorsHandler {

    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<ResponseErrorMessage> handleMinioUploadException(MinioUploadException e) {

        return ResponseEntity.internalServerError().body(new ResponseErrorMessage("Failed to upload file: " + e.getMessage()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseErrorMessage> handleNoSuchElementException(FileNotFoundException e) {

        return new ResponseEntity<>(new ResponseErrorMessage(e.getMessage()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MinioDownloadException.class)
    public ResponseEntity<ResponseErrorMessage> handleMinioDownloadException(MinioDownloadException e) {

        return ResponseEntity.internalServerError().body(new ResponseErrorMessage("Failed to download file: " + e.getMessage()));
    }

    @ExceptionHandler(DownloadStatusException.class)
    public ResponseEntity<ResponseErrorMessage> handleDownloadStatusException(DownloadStatusException e) {

        return ResponseEntity.badRequest().body(new ResponseErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(SubscriptionStatusException.class)
    public ResponseEntity<ResponseErrorMessage> handleSubscriptionStatusException(SubscriptionStatusException e) {

        return ResponseEntity.badRequest().body(new ResponseErrorMessage(e.getMessage()));
    }

}
