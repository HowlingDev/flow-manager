package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileConversionEvent {

    private UUID eventId;
    private String message;
    private Status status;

    public enum Status {
        CONVERTED,
        FAILED
    }
}
