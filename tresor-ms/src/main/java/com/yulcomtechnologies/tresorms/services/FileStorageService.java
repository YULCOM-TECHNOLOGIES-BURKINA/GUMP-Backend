package com.yulcomtechnologies.tresorms.services;


import com.yulcomtechnologies.tresorms.entities.File;

import java.io.IOException;

public interface FileStorageService {
    String getPath(File file);
    void saveFile(byte[] fileContent, String filePath) throws IOException;
}
