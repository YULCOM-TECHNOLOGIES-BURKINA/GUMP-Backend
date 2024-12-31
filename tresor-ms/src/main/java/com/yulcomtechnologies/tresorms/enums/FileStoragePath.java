package com.yulcomtechnologies.tresorms.enums;

public enum FileStoragePath {


  Request_URL("https://gump-gateway.yulpay.com/api/files/");
 //   Request_URL("http://localhost:9090/api/files/");

    private String path;

    FileStoragePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}