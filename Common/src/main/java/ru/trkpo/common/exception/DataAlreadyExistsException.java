package ru.trkpo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
