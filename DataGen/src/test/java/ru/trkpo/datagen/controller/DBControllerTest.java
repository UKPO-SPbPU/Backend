package ru.trkpo.datagen.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import ru.trkpo.datagen.service.DBService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DBControllerTest {

    @Mock
    private DBService dbServiceMock;

    @InjectMocks
    private DBController underTestController;

    @Test
    void testResetShouldBeSuccessful() {
        doNothing().when(dbServiceMock).reset();
        ResponseEntity<Boolean> responseEntity = underTestController.reset();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
        verify(dbServiceMock, times(1)).reset();
    }

    @Test
    void testResetShouldThrowException() {
        doThrow(new RuntimeException("Simulated exception")).when(dbServiceMock).reset();
        assertThatThrownBy(underTestController::reset).isInstanceOf(ResponseStatusException.class);
        verify(dbServiceMock, times(1)).reset();
    }

    @Test
    void testPopulateShouldBeSuccessful() {
        doNothing().when(dbServiceMock).populate();
        ResponseEntity<Boolean> responseEntity = underTestController.populate();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
        verify(dbServiceMock, times(1)).populate();
    }

    @Test
    void testPopulateShouldThrowException() {
        doThrow(new RuntimeException("Simulated exception")).when(dbServiceMock).populate();
        assertThatThrownBy(underTestController::populate).isInstanceOf(ResponseStatusException.class);
        verify(dbServiceMock, times(1)).populate();
    }

    @Test
    void testTruncateShouldBeSuccessful() {
        doNothing().when(dbServiceMock).truncate();
        ResponseEntity<Boolean> responseEntity = underTestController.truncate();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
        verify(dbServiceMock, times(1)).truncate();
    }

    @Test
    void testTruncateShouldThrowException() {
        doThrow(new RuntimeException("Simulated exception")).when(dbServiceMock).truncate();
        assertThatThrownBy(underTestController::truncate).isInstanceOf(ResponseStatusException.class);
        verify(dbServiceMock, times(1)).truncate();
    }
}
