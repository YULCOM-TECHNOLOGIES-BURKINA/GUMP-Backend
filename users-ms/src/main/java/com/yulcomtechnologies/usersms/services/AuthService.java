package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.entities.Company;
import com.yulcomtechnologies.usersms.entities.File;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.events.AccountStateChanged;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.FileRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final SsoProvider ssoProvider;
    private final CorporationInfosExtractor corporationInfosExtractor;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EventPublisher eventPublisher;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void validatePendingUserAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        ssoProvider.activateUser(user.getKeycloakUserId());
        user.setIsActive(true);
        user.setIsPendingForActivation(false);
        userRepository.save(user);
        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }

    @Transactional
    public void toglleUserAccountState(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        ssoProvider.toggleUserAccount(user.getKeycloakUserId(),!user.getIsActive());
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }


    @Transactional
    public void register(
        RegisterRequest registerRequest,
        MultipartFile cnibFile,
        MultipartFile statutFile
    ) throws Exception {
        CorporationData corporationData = corporationInfosExtractor.extractCorporationInfos(registerRequest.getIfuNumber()).orElseThrow(
            () -> new Exception("Corporation not found")
        );

        var user = userRepository.findByUsername(registerRequest.getIfuNumber());

        if (user.isPresent()) {
            throw new BadRequestException("Un compte est déjà associé à cet IFU");
        }

        Company corporation = createCompany(registerRequest, corporationData, cnibFile, statutFile);

        var ssoId = ssoProvider.createUser(
            new CreateUserCommand(
                registerRequest.getIfuNumber(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getIfuNumber(),
                registerRequest.getIfuNumber(),
                true,
                false,
                UserRole.USER,
                UserType.USER
            )
        );
        createUser(registerRequest, ssoId, corporation);
    }

    private void createUser(RegisterRequest registerRequest, String ssoId, Company corporation) {
        var user = User.builder()
            .email(registerRequest.getEmail())
            .role(UserRole.USER)
            .isActive(false)
            .isPendingForActivation(true)
            .region(registerRequest.region)
            .username(registerRequest.getIfuNumber())
            .cnssNumber(registerRequest.getCnssNumber())
            .keycloakUserId(ssoId)
            .userType(UserType.USER)
            .company(corporation)
            .build();

        userRepository.save(user);
    }

    private Company createCompany(
        RegisterRequest registerRequest,
        CorporationData corporationData,
        MultipartFile cnibFile,
        MultipartFile statutFile
    ) throws IOException {
        var corporation = Company.builder()
            .address(corporationData.address())
            .email(corporationData.email())
            .rccm(corporationData.rccmNumber())
            .ifu(registerRequest.getIfuNumber())
            .nes(registerRequest.getNes())
            .location(corporationData.location())
            .representantPhone(registerRequest.getRepresentantPhone())
            .representantFirstname(registerRequest.getRepresentantFirstname())
            .representantLastname(registerRequest.getRepresentantLastname())
            .name(corporationData.name())
            .enterpriseStatut(saveFile(statutFile, "Statut entreprise"))
            .idDocument(saveFile(cnibFile, "CNIB"))
            .phone(corporationData.phoneNumber())
            .build();

        companyRepository.save(corporation);
        return corporation;
    }

    public void rejectAccountCreation(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }

    private File saveFile(MultipartFile file, String label) throws IOException {
        String UPLOAD_DIR = "uploads/";
        Path filePath = Paths.get(UPLOAD_DIR, UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().replace(" ", ""));
        fileStorageService.saveFile(file.getBytes(), filePath.toString());

        File fileEntity = new File(label, filePath.toString());

        return fileRepository.save(fileEntity);
    }
}
