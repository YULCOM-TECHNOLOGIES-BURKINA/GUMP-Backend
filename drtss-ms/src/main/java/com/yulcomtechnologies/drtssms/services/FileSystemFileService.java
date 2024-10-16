package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.entities.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileSystemFileService implements FileService {
    private final String appUrl;

    public FileSystemFileService(@Value("${app..url}") String uploadDir) {
        this.appUrl = uploadDir;
    }

    @Override
    public String getPath(File file) {
        return appUrl + "/files/" + file.getId() + "/" + file.getPath().toLowerCase().replace("/", "-");
    }
}
