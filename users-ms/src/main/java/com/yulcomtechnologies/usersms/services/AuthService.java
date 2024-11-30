package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.entities.Company;
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

@Service
@AllArgsConstructor
public class AuthService {
    private final SsoProvider ssoProvider;
    private final CorporationInfosExtractor corporationInfosExtractor;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EventPublisher eventPublisher;
    private final FileRepository fileRepository;

    @Transactional
    public void validatePendingUserAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );


        ssoProvider.activateUser(user.getKeycloakUserId());
        user.setIsActive(true);
        userRepository.save(user);
        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }

    @Transactional
    public void register(RegisterRequest registerRequest) throws Exception {
        CorporationData corporationData = corporationInfosExtractor.extractCorporationInfos(registerRequest.getIfuNumber()).orElseThrow(
            () -> new Exception("Corporation not found")
        );

        var user = userRepository.findByUsername(registerRequest.getIfuNumber());

        if (user.isPresent()) {
            throw new BadRequestException("Un compte est déjà associé à cet IFU");
        }

        Company corporation = createCompany(registerRequest, corporationData);

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
            .username(registerRequest.getIfuNumber())
            .cnssNumber(registerRequest.getCnssNumber())
            .keycloakUserId(ssoId)
            .userType(UserType.USER)
            .company(corporation)
            .build();

        userRepository.save(user);
    }

    private Company createCompany(RegisterRequest registerRequest, CorporationData corporationData) {
        var corporation = Company.builder()
            .address(corporationData.address())
            .email(corporationData.email())
            .rccm(corporationData.rccmNumber())
            .ifu(registerRequest.getIfuNumber())
            .name(corporationData.name())
            .phone(corporationData.phoneNumber())
            .build();

        companyRepository.save(corporation);
        return corporation;
    }

    public void rejectAccountCreation(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        var company = user.getCompany();
        companyRepository.delete(company);
        userRepository.delete(user);

        if (company.getEnterpriseStatut() != null) {
            fileRepository.delete(company.getEnterpriseStatut());
        }

        if (company.getIdDocument() != null) {
            fileRepository.delete(company.getIdDocument());
        }

        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }
}
