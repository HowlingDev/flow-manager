package com.example.controllers;

import com.example.entities.FileEntity;
import com.example.services.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileProcessingService fileProcessingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

            return ResponseEntity.ok(fileProcessingService.processFile(file));
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

        StreamingResponseBody responseBody = fileProcessingService.downloadFile(entity.getFileName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"document.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(responseBody);
    }
}
