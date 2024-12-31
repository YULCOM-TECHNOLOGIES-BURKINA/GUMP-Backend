package com.yulcomtechnologies.drtssms.enums;

public enum FileStoragePath {


  Request_URL("https://gump-gateway.yulpay.com/api/files/"),
    // Request_URL("http://localhost:9090/api/files/"),
     SCAN_SIGN_PATH("./uploads/signatures_electronique/signatures_img"),

    CERTIFICAT_PATH("./uploads/signatures_electronique/certificats");

    private String path;

    FileStoragePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}