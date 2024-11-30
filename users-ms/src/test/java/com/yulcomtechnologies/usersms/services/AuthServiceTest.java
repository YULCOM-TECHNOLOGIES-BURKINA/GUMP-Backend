package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.entities.Company;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.events.AccountStateChanged;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    EventPublisher eventPublisher;


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
            null,
            "BFOUA2016B4661"
        );

        when(corporationInfosExtractor.extractCorporationInfos(registrationRequest.ifuNumber)).thenReturn(
            Optional.of(corporationData)
        );

        when(ssoProvider.createUser(any())).thenReturn("OMEGA_LAMBDA_7_XL_9");

        authService.register(registrationRequest);

        var company = Company.builder()
            .ifu(registrationRequest.ifuNumber)
            .name(corporationData.name())
            .address(corporationData.address())
            .email(corporationData.email())
            .phone(corporationData.phoneNumber())
            .rccm("BFOUA2016B4661")
            .build();

        verify(companyRepository).save(
           company
        );

        verify(userRepository).save(
          User.builder()
              .role(UserRole.USER)
              .userType(UserType.USER)
              .isActive(false)
              .cnssNumber(registrationRequest.cnssNumber)
              .email(registrationRequest.email)
              .username(registrationRequest.ifuNumber)
              .keycloakUserId("OMEGA_LAMBDA_7_XL_9")
              .company(company)
              .build()
        );

    }

    @Test
    void validatesPendingUserAccount() throws Exception {
        var userId = 1L;
        var user = User.builder().id(userId).email("johndo@gmail.com").keycloakUserId("OMEGA_LAMBDA_7_XL_9").isActive(false).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        authService.validatePendingUserAccount(userId);

        assertTrue(user.getIsActive());

        verify(userRepository).save(user);
        verify(ssoProvider).activateUser("OMEGA_LAMBDA_7_XL_9");

        verify(eventPublisher).dispatch(new AccountStateChanged(user.getId()));

    }

    @Test
    void rejectAccountCreation() {
        var userId = 1L;
        var user = User.builder().id(userId)
            .company(Company.builder().id(1L).build())
            .email("johndo@gmail.com").keycloakUserId("OMEGA_LAMBDA_7_XL_9").isActive(false).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        authService.rejectAccountCreation(userId);

        verify(userRepository).delete(user);
        verify(companyRepository).delete(user.getCompany());

        verify(eventPublisher).dispatch(new AccountStateChanged(user.getId()));
    }
}
