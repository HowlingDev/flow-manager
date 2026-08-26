package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ConvertFileToPdfEvent {

    private UUID eventId;
    private String fileUrl;
}
