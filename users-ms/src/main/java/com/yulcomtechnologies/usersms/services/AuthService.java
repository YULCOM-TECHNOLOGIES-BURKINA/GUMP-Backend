package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.entities.Company;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
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

    @Transactional
    public void register(RegisterRequest registerRequest) throws Exception {
        CorporationData corporationData = corporationInfosExtractor.extractCorporationInfos(registerRequest.getIfuNumber()).orElseThrow(
            () -> new Exception("Corporation not found")
        );

        //Handle other paths

        var createdUserId = ssoProvider.createUser(
            new CreateUserCommand(
                registerRequest.getIfuNumber(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getIfuNumber(),
                registerRequest.getIfuNumber(),
                true,
                true,
                UserRole.USER,
                UserType.USER
            )
        );

        Company corporation = createCompany(registerRequest, corporationData);

        createUser(registerRequest, createdUserId, corporation);
    }

    private void createUser(RegisterRequest registerRequest, String createdUserId, Company corporation) {
        var user = User.builder()
            .email(registerRequest.getEmail())
            .keycloakUserId(createdUserId)
            .role(UserRole.USER)
            .userType(UserType.USER)
            .company(corporation)
            .build();

        userRepository.save(user);
    }

    private Company createCompany(RegisterRequest registerRequest, CorporationData corporationData) {
        var corporation = Company.builder()
            .address(corporationData.address())
            .email(corporationData.email())
            .ifu(registerRequest.getIfuNumber())
            .name(corporationData.name())
            .phone(corporationData.phoneNumber())
            .build();

        companyRepository.save(corporation);
        return corporation;
    }
}
