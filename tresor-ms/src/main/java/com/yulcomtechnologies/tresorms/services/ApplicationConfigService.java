package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.tresorms.dtos.ApplicationConfigDto;
import com.yulcomtechnologies.tresorms.dtos.UpdateApplicationConfigRequest;
import com.yulcomtechnologies.tresorms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.tresorms.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ApplicationConfigService {
    private final ApplicationConfigRepository applicationConfigRepository;
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;

    public ApplicationConfigDto getApplicationConfig() {
        var applicationConfig = applicationConfigRepository.get();
        var dto = new ApplicationConfigDto();
        dto.setLogo(fileStorageService.getPath(applicationConfig.getLogo()));
        BeanUtils.copyProperties(applicationConfig, dto);

        return dto;
    }

    public void updateApplicationConfig(UpdateApplicationConfigRequest request, MultipartFile logoFile) throws IOException {
        var applicationConfig = applicationConfigRepository.get();
        BeanUtils.copyProperties(request, applicationConfig);

        if (logoFile != null) {
            var newFilePath = "uploads/" + UUID.randomUUID() + ".png";

            fileStorageService.saveFile(logoFile.getBytes(), newFilePath);

            applicationConfig.getLogo().setPath(newFilePath);
            fileRepository.save(applicationConfig.getLogo());
        }

        applicationConfigRepository.save(applicationConfig);
    }
}
