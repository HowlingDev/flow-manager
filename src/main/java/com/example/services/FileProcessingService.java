package com.example.services;

import com.example.entities.FileEntity;
import com.example.events.FileConversionEvent;
import com.example.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileRepository fileRepository;

    public void processFile(FileEntity entity) {
        fileRepository.save(entity);
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
}
