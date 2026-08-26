package com.example.services;

import com.example.entities.FileEntity;
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
}
