package com.yulcomtechnologies.config;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<Error> errors;
}
