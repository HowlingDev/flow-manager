package com.example.services;

import com.example.entities.FileEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.events.FileConversionEvent;
import com.example.exceptions.MinioDownloadException;
import com.example.exceptions.MinioUploadException;
import com.example.repositories.FileRepository;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileRepository fileRepository;
    private final MinioService minioService;
    private final KafkaTemplate<String, ConvertFileToPdfEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.convert-event}")
    private String convertTopic;

    public String processFile(MultipartFile file) {
        try {
            minioService.upload(file, file.getOriginalFilename());
            UUID uuid = UUID.randomUUID();
            FileEntity entity = new FileEntity(
                    uuid,
                    file.getOriginalFilename(),
                    FileEntity.Status.PROCESSING
            );
            ConvertFileToPdfEvent event = new ConvertFileToPdfEvent(uuid, file.getOriginalFilename());
            kafkaTemplate.send(convertTopic, event);
            fileRepository.save(entity);
            return uuid.toString();
        } catch (IOException | MinioException e) {
            throw new MinioUploadException(e.getMessage());
        }

    }

    public void processSuccessEvent(FileConversionEvent event) {
        FileEntity entity = fileRepository.findById(event.getEventId()).orElseThrow();
        entity.setStatus(FileEntity.Status.SUCCESS);
        entity.setFileName(event.getMessage());
        fileRepository.save(entity);
    }

    public void processFailedEvent(FileConversionEvent event) {
        FileEntity entity = fileRepository.findById(event.getEventId()).orElseThrow();
        entity.setStatus(FileEntity.Status.FAIL);
        fileRepository.save(entity);
    }

    public FileEntity findFileById(UUID uuid) {
        return fileRepository.findById(uuid).orElseThrow();
    }

    public StreamingResponseBody downloadFile(String fileName) {
        return outputStream -> {
            try (InputStream stream = minioService.download(fileName)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (MinioException e) {
                throw new MinioDownloadException(e.getMessage());
            }
        };
    }
}
