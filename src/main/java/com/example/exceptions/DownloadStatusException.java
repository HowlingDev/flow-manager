package com.example.exceptions;

public class DownloadStatusException extends RuntimeException {

    public DownloadStatusException(String message) {
        super(message);
    }
}
