package com.yulcomtechnologies.usersms;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
//Find a way to use this test containers
public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired CompanyRepository companyRepository;

    @Test
    void registersSuccessfully() throws Exception {
        mockMvc
            .perform(
                post("/auth/register")
                    .content(objectMapper.writeValueAsString(
                        new RegisterRequest(
                            "00077218Y",
                            "12345678",
                            "12345678",
                            "arnaud.bakyono@gmail.com",
                            "1234"
                        )
                    ))
                    .contentType("application/json")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}
