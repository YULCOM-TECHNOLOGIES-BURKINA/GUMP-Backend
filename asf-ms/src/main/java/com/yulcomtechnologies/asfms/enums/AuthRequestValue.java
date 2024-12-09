package com.yulcomtechnologies.asfms.enums;

public enum AuthRequestValue {
    E_SINTAXE("https://staging.esintax.bf/"),
    ASF_AUTH_EMAIL("testapi@gump.bf"),
    ASF_AUTH_PASSWORD("111222333");

    private final String value;
    AuthRequestValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

