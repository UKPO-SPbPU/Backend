package ru.trkpo.crm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CRMExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Internal Server Error: " + e.getMessage());
        return new ResponseEntity<>(
                "Internal Server Error: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
