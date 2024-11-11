package com.yulcomtechnologies.usersms;

import com.yulcomtechnologies.usersms.entities.Company;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UsersControllerTest extends BaseIntegrationTest {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    void getsUsersList() throws Exception {
        var company = companyRepository.save(
            Company.builder()
                .name("Yulcom Technologies")
                .ifu("ifu")
                .email("mail@test.com")
                .phone("12345678")
                .address("Ouaga")
                .build()
        );

        var user = userRepository.save(
            User.builder()
                .id(1L)
                .region("CENTRE")
                .username("ifu")
                .keycloakUserId(UUID.randomUUID().toString())
                .email("yulcom@gmail.com")
                .userType(UserType.USER)
                .forename("Yulcom")
                .lastname("Yulcom")
                .role(UserRole.USER)
                .company(company)
                .build()
        );

        mockMvc
            .perform(
                get("/users")
                    .contentType("application/json")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").value(user.getId()))
            .andExpect(jsonPath("$.content[0].forename").value("Yulcom"))
            .andExpect(jsonPath("$.content[0].lastname").value("Yulcom"))
            .andExpect(jsonPath("$.content[0].email").value("yulcom@gmail.com"))
            .andExpect(jsonPath("$.content[0].role").value("USER"))
            .andExpect(jsonPath("$.content[0].userType").value("USER"))
            .andExpect(jsonPath("$.content[0].region").value("CENTRE"))
            .andExpect(jsonPath("$.content[0].username").value("ifu"))
            .andExpect(jsonPath("$.content[0].company.name").value("Yulcom Technologies"))
            .andExpect(jsonPath("$.content[0].company.ifu").value("ifu"))
            .andExpect(jsonPath("$.content[0].company.address").value("Ouaga"));;
    }
}
