package com.example.services;

import com.example.entities.FileEntity;
import com.example.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional(readOnly = true)
    public Optional<FileEntity> findById(UUID id) {
        return fileRepository.findById(id);
    }

    @Transactional
    public void saveFile(FileEntity entity) {
        fileRepository.save(entity);
    }
}
