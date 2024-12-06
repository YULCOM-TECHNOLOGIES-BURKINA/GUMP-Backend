package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Document {
    private String folder;
    private String filename;
    private String fileType;
    private String fileExtension;
    private String dossier;
}
