package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.entities.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemFileStorageService implements FileStorageService {
    private final String appUrl;

    public FileSystemFileStorageService(@Value("${app..url}") String uploadDir) {
        this.appUrl = uploadDir;
    }

    @Override
    public String getPath(File file) {
        return appUrl + "/files/" + file.getId() + "/" + file.getPath().toLowerCase().replace("/", "-");
    }

    @Override
    public void saveFile(byte[] fileContent, String path) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, fileContent);
    }
}
