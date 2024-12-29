package com.yulcomtechnologies.drtssms.services;


import com.yulcomtechnologies.drtssms.dtos.AttestationConfigDto;
import com.yulcomtechnologies.drtssms.dtos.ParamsConfigActeDto;
import com.yulcomtechnologies.drtssms.dtos.UpdateAttestationConfigDto;
import com.yulcomtechnologies.drtssms.dtos.UpdateParamsActeDto;
import com.yulcomtechnologies.drtssms.entities.AttestationConfig;
import com.yulcomtechnologies.drtssms.entities.ParamsConfigActe;
import com.yulcomtechnologies.drtssms.repositories.AttestationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.ParamsConfigActeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttestationConfigService {

    private final AttestationConfigRepository attestationConfigRepository;
    private final ParamsConfigActeRepository paramsConfigActeRepository;

    public AttestationConfig getAttestationConfig() {
        return attestationConfigRepository.findById(1L).orElse(null);
    }

   /* public ParamsConfigActe getParamsConfigActe() {
        return paramsConfigActeRepository.findById(1L).orElse(null);
    }*/



    public List<AttestationConfigDto> getParamsConfigActe() {
        return attestationConfigRepository.findAll()
                .stream()
                .map(config -> AttestationConfigDto.builder()
                        .id(config.getId())
                        .logo(config.getLogo())
                        .icone(config.getIcone())
                        .title(config.getTitle())
                        .code(config.getCode())
                        .description(config.getDescription())
                        .acteConfig(config.getActeConfig().stream()
                                .map(acte -> new ParamsConfigActeDto(
                                        acte.getId(),
                                        acte.getParam(),
                                        acte.getLabelle(),
                                        acte.getValue()
                                ))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public void UpdateAttestationGlobalInfo(UpdateAttestationConfigDto updateAttestationConfigDto, MultipartFile logoFile) {
         AttestationConfig attestationConfig = attestationConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("AttestationConfig not found"));

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                 String uploadDirectory = "uploads/";

                 File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    boolean created = directory.mkdir();
                    if (!created) {
                        throw new RuntimeException("Le répertoire d'upload n'a pas pu être créé.");
                    }
                }

                String fileName = logoFile.getOriginalFilename();
                if (fileName == null || !fileName.matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new RuntimeException("Le fichier doit être une image (png, jpg, jpeg).");
                }

                String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;
                Path filePath = Paths.get(uploadDirectory, uniqueFileName);

                Files.copy(logoFile.getInputStream(), filePath);

                updateAttestationConfigDto.setLogo(filePath.toString());
                attestationConfig.setLogo(filePath.toString());

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du logo : " + e.getMessage());
            }
        }
        attestationConfig.setDescription(updateAttestationConfigDto.getDescription());
        attestationConfig.setIcone(updateAttestationConfigDto.getIcone());
        attestationConfig.setTitle(updateAttestationConfigDto.getTitle());

        attestationConfigRepository.save(attestationConfig);
    }

    public ParamsConfigActeDto udpateParamActeConfig(UpdateParamsActeDto paramsConfigActeDto) {

        ParamsConfigActe updateParam = paramsConfigActeRepository.findById(paramsConfigActeDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Aucun paramètre correspondant trouvé"));
        updateParam.setValue(paramsConfigActeDto.getValue());
        updateParam = paramsConfigActeRepository.save(updateParam);
        return new ParamsConfigActeDto(
                updateParam.getId(),
                updateParam.getParam(),
                updateParam.getLabelle(),
                updateParam.getValue()
        );
    }


}
