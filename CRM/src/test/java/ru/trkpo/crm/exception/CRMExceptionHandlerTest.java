package ru.trkpo.crm.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CRMExceptionHandlerTest {

    private static final CRMExceptionHandler underTestHandler = new CRMExceptionHandler();

    @Test
    void testHandleExceptionShouldReturnResponseEntityWithInternalServerError() {
        Exception exception = new Exception("Test exception");
        ResponseEntity<String> resultResponse = underTestHandler.handleException(exception);
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
