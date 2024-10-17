package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.entities.File;

import java.io.IOException;

public interface FileStorageService {
    String getPath(File file);
    void saveFile(byte[] fileContent, String filePath) throws IOException;
}
