package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.entities.Company;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    SsoProvider ssoProvider;

    @Mock
    CorporationInfosExtractor corporationInfosExtractor;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    UserRepository userRepository;


    @Test
    void registersSuccessfully() throws Exception {
        var registrationRequest = new RegisterRequest(
            "ifuNumber",
            "password",
            "password",
            "lupin.arsene@gmail.com",
            "1234"
        );

        var corporationData = new CorporationData(
            "Yulcom",
            "Ouaga",
            "1234",
            "mail@test.com",
            null
        );

        when(corporationInfosExtractor.extractCorporationInfos(registrationRequest.ifuNumber)).thenReturn(
            Optional.of(corporationData)
        );

        when(ssoProvider.createUser(new CreateUserCommand(
            registrationRequest.ifuNumber,
            registrationRequest.password,
            registrationRequest.email,
            registrationRequest.ifuNumber,
            registrationRequest.ifuNumber,
            true,
            true,
            UserRole.USER,
            UserType.USER
        ))).thenReturn("123456");


        authService.register(registrationRequest);

        var company =  new Company(
            registrationRequest.ifuNumber,
            corporationData.name(),
            corporationData.address(),
            corporationData.email(),
            corporationData.phoneNumber()
        );

        verify(companyRepository).save(
           company
        );

        verify(userRepository).save(
          User.builder()
              .role(UserRole.USER)
              .userType(UserType.USER)
              .cnssNumber(registrationRequest.cnssNumber)
              .email(registrationRequest.email)
              .username(registrationRequest.ifuNumber)
              .keycloakUserId("123456")
              .company(company)
              .build()
        );

    }
}
