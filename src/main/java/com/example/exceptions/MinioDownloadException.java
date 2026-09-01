package com.example.exceptions;

public class MinioDownloadException extends RuntimeException {

    public MinioDownloadException(String message) {
        super(message);
    }
}
