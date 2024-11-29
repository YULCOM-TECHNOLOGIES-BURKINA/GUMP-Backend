package com.yulcomtechnologies.usersms;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import com.yulcomtechnologies.usersms.services.SsoProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired CompanyRepository companyRepository;

    @MockBean
    SsoProvider ssoProvider;

    @Autowired
    UserRepository userRepository;

    @Test
    void registersSuccessfully() throws Exception {
        when(ssoProvider.createUser(any())).thenReturn("12345678");

        mockMvc
            .perform(
                post("/auth/register")
                    .content(objectMapper.writeValueAsString(
                        new RegisterRequest(
                            "00077218Y",
                            "12345678",
                            "12345678",
                            "12345678",
                            "arnaud.bakyono@gmail.com"
                        )
                    ))
                    .contentType("application/json")
            )
            .andDo(print())
            .andExpect(status().isOk());

        var user = userRepository.findByUsername("00077218Y").orElseThrow();

        assertEquals("12345678", user.getCnssNumber());
        assertFalse(user.getIsActive());
        assertEquals("arnaud.bakyono@gmail.com", user.getEmail());
        assertEquals(UserType.USER, user.getUserType());
        assertEquals(UserRole.USER, user.getRole());
    }
}
