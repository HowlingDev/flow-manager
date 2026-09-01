package com.example.services;

import com.example.entities.FileEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.events.FileConversionEvent;
import com.example.exceptions.DownloadStatusException;
import com.example.exceptions.FileNotFoundException;
import com.example.exceptions.MinioDownloadException;
import com.example.exceptions.MinioUploadException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileService fileService;
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
            fileService.saveFile(entity);
            return uuid.toString();
        } catch (IOException | MinioException e) {
            throw new MinioUploadException(e.getMessage());
        }

    }

    public void processSuccessEvent(FileConversionEvent event) {
        FileEntity entity = findFileById(event.getEventId());
        entity.setStatus(FileEntity.Status.SUCCESS);
        entity.setFileName(event.getMessage());
        fileService.saveFile(entity);
    }

    public void processFailedEvent(FileConversionEvent event) {
        FileEntity entity = findFileById(event.getEventId());
        entity.setStatus(FileEntity.Status.FAIL);
        fileService.saveFile(entity);
    }

    public FileEntity findFileById(UUID uuid) {

        Optional<FileEntity> entity = fileService.findById(uuid);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new FileNotFoundException(String.format("Файл с %s не найден", uuid));
        }
    }

    public FileEntity.Status getStatus(UUID uuid)  {
        return findFileById(uuid).getStatus();
    }

    public StreamingResponseBody downloadFile(UUID uuid) {
        FileEntity entity = findFileById(uuid);
        if (entity.getStatus() != FileEntity.Status.SUCCESS) {
            throw new DownloadStatusException("Файл не готов к загрузке. Проверьте статус и попробуйте позже");
        }
        return outputStream -> {
            try (InputStream stream = minioService.download(entity.getFileName())) {
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
