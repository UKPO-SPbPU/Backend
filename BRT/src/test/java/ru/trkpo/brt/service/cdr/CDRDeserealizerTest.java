package ru.trkpo.brt.service.cdr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDR;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.springframework.expression.ParseException;

@ExtendWith(MockitoExtension.class)
public class CDRDeserealizerTest {

    @Mock
    private BufferedReader readerMock;

    @InjectMocks
    private final CDRDeserializer underTestDeserializer = new CDRDeserializer();

    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @BeforeEach
    void setUpClass() {
        setField(underTestDeserializer, "dateTimeFormat", DATE_TIME_FORMAT);
    }

    @Test
    void testDeserializeShouldReturnCDR() throws IOException {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "71112223344";
        String testCallTypeCode = "02";

        String CDRString = testCallTypeCode + ", " +
                testPhoneNumber + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter);

        when(readerMock.readLine()).thenReturn(CDRString);

        // Act
        Optional<CDR> resultCDR = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultCDR.isPresent()).isTrue();
        assertThat(resultCDR.get().getCallTypeCode()).isEqualTo(testCallTypeCode);
        assertThat(resultCDR.get().getPhoneNumber()).isEqualTo(testPhoneNumber);
        assertThat(resultCDR.get().getStartDateTime()).isEqualTo(testStartDateTime);
        assertThat(resultCDR.get().getEndDateTime()).isEqualTo(testEndDateTime);

        verify(readerMock, times(1)).readLine();
    }

    @Test
    void testDeserializeShouldThrowParseException() throws IOException {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "71112223344";
        String testCallTypeCode = "02";

        String CDRString = testCallTypeCode + ", " +
                testPhoneNumber + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter) + ", EXTRA_INFORMATION";

        when(readerMock.readLine()).thenReturn(CDRString);

        // Act & Assert
        assertThrows(ParseException.class, () -> {
            underTestDeserializer.deserialize(readerMock);
        });

        verify(readerMock, times(1)).readLine();
    }


    @Test
    void testDeserializeShouldReturnEmptyOptional() throws IOException {
        // Arrange
        when(readerMock.readLine()).thenReturn(null);

        // Act
        Optional<CDR> resultCDR = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultCDR.isEmpty()).isTrue();

        verify(readerMock, times(1)).readLine();
    }
}
