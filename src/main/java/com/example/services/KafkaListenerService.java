package com.example.services;

import com.example.events.FileConversionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final FileProcessingService fileProcessingService;

    @KafkaListener(
            topics = "${spring.kafka.topics.success-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleSuccessEvent(FileConversionEvent event) {
        fileProcessingService.processSuccessEvent(event);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.failed-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleFailedEvent(FileConversionEvent event) {
        fileProcessingService.processFailedEvent(event);
    }
}
