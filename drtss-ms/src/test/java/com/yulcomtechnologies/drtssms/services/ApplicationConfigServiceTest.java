package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ApplicationConfigServiceTest extends BaseIntegrationTest {
    ApplicationConfigService applicationConfigService;

    @Autowired
    ApplicationConfigRepository applicationConfigRepository;

    @Autowired
    FileRepository fileRepository;

    @MockBean
    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        applicationConfigService = new ApplicationConfigService(
            applicationConfigRepository,
            fileStorageService,
            fileRepository
        );
    }

    @Test
    void getApplicationConfig() {
        when(fileStorageService.getPath(any())).thenReturn(
            "https://fake-url.com"
        );

        var result = applicationConfigService.getApplicationConfig();
        assertEquals("https://fake-url.com", result.getLogo());
    }
}
