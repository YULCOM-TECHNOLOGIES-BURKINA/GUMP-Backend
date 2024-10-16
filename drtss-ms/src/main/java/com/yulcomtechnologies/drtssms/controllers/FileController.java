package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@AllArgsConstructor
public class FileController {
    private final FileRepository fileRepository;

    @GetMapping("/files/{id}/{path}")
    public ResponseEntity<Resource> getFileById(@PathVariable Long id) {
        var fileEntity = fileRepository.findById(id).get();

        if (fileEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Construct the full file path
        Path filePath = Paths.get("", fileEntity.getPath());
        File file = filePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Serve the file as a resource
        Resource resource = new FileSystemResource(file);

        // Determine the content type (optional, depending on your use case)
        String contentType = "application/octet-stream"; // Default to binary

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

        // Return the file as a download
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }
}
