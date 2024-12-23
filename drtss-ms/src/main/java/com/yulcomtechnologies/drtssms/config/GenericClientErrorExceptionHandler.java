package com.yulcomtechnologies.drtssms.config;

import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GenericClientErrorExceptionHandler {

    @ExceptionHandler({BadRequestException.class, ResourceNotFoundException.class})
    public ResponseEntity<CustomErrorResponse> handleValidationException(Exception ex) {
        CustomErrorResponse response = new CustomErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(
            ex instanceof BadRequestException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND
        ).body(response);
    }
}
