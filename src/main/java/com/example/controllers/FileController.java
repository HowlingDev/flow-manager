package com.example.controllers;

import com.example.entities.FileEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.services.FileProcessingService;
import com.example.services.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;
    private final FileProcessingService fileProcessingService;
    private final KafkaTemplate<String, ConvertFileToPdfEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.convert-event}")
    private String convertTopic;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            minioService.upload(file, file.getOriginalFilename());
            UUID uuid = UUID.randomUUID();
            FileEntity entity = new FileEntity(
                    uuid,
                    file.getOriginalFilename(),
                    FileEntity.Status.PROCESSING
            );
            fileProcessingService.processFile(entity);
            ConvertFileToPdfEvent event = new ConvertFileToPdfEvent(uuid, file.getOriginalFilename());
            kafkaTemplate.send(convertTopic, event);
            return uuid.toString();
        } catch (Exception e) {
            return "Произошла ошибка";
        }
    }
}
