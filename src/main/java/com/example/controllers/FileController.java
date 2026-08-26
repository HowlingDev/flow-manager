package com.example.controllers;

import com.example.entities.FileEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.services.FileProcessingService;
import com.example.services.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
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

    @GetMapping("/status/{id}")
    public String getStatus(@PathVariable UUID id) {
        return fileProcessingService.findFileById(id).getStatus().toString();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable UUID id) {
        FileEntity entity = fileProcessingService.findFileById(id);
        if (entity.getStatus() != FileEntity.Status.SUCCESS) {
            return ResponseEntity.badRequest().build();
        }

        try {
            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream stream = minioService.download(entity.getFileName())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = stream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"document.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
