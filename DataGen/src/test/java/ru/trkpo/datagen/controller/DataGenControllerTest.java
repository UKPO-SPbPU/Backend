package ru.trkpo.datagen.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import ru.trkpo.datagen.service.DataGenService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataGenControllerTest {
    @Mock
    private DataGenService dataGenServiceMock;

    @InjectMocks
    private DataGenController underTestController;

    @Test
    void testGenerateCDRsShouldBeSuccessful() throws Exception {
        doNothing().when(dataGenServiceMock).generateCDRs();
        ResponseEntity<Boolean> responseEntity = underTestController.generateCDRs();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
        verify(dataGenServiceMock, times(1)).generateCDRs();
    }

    @Test
    void testGenerateCDRsShouldThrowException() throws IOException {
        doThrow(new IOException("Simulated exception")).when(dataGenServiceMock).generateCDRs();
        assertThatThrownBy(underTestController::generateCDRs).isInstanceOf(ResponseStatusException.class);
        verify(dataGenServiceMock, times(1)).generateCDRs();
    }
}
