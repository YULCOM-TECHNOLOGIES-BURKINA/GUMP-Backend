package com.yulcomtechnologies.anpems.enums;

public enum AuthRequestValue {
    ANPE_URL("https://attestation.anpe.gov.bf"),
    ANPE_AUTH_EMAIL("yulcum-tech@gmail.com"),
    ANPE_AUTH_PASSWORD("password");

    private final String value;
    AuthRequestValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

