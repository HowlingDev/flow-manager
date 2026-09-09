package com.example.exceptions;

public class SubscriptionStatusException extends RuntimeException {

    public SubscriptionStatusException(String message) {
        super(message);
    }
}
