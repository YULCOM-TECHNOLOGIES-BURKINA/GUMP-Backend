package com.yulcomtechnologies.usersms.services;

import java.util.Optional;

public interface CorporationInfosExtractor {
    Optional<CorporationData> extractCorporationInfos(String ifuNumber) throws Exception;
}
