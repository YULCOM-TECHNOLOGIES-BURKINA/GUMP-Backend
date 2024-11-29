package com.yulcomtechnologies.usersms.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScrapingCorporationInfosExtractorTest {
    ScrapingCorporationInfosExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new ScrapingCorporationInfosExtractor();
    }

    @Test
    void retrieveDataOfExistingCompany() throws Exception {
        var result = extractor.extractCorporationInfos("00077218Y");

        Assertions.assertEquals(
            new CorporationData(
                "YULCOM TECHNOLOGIES SARL",
                "01 BP 77 OUAGA 01",
                "70800839",
                "",
                null,
                "BFOUA2016B4661"
            ),
            result.get()
        );
    }

    @Test
    void failsToRetrieveDataOfInexistingCompany() throws Exception {
        var result = extractor.extractCorporationInfos("00077218Y-1");

        Assertions.assertTrue(
            result.isEmpty()
        );
    }
}
