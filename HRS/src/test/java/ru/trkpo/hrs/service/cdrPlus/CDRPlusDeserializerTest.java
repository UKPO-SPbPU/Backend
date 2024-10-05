package ru.trkpo.hrs.service.cdrPlus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDRPlus;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.springframework.expression.ParseException;

@ExtendWith(MockitoExtension.class)
public class CDRPlusDeserializerTest {

    @Mock
    private BufferedReader readerMock;

    @InjectMocks
    private CDRPlusDeserializer underTestDeserializer;

    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @BeforeEach
    void setUpClass() {
        setField(underTestDeserializer, "dateTimeFormat", DATE_TIME_FORMAT);
    }

    @Test
    void testDeserializeShouldReturnCDRPlus() throws IOException {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "73333333333";
        String testCallTypeCode = "02";
        String testTariffCode = "03";

        String CDRPlusString = testCallTypeCode + ", " +
                testPhoneNumber + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter) + ", " +
                Duration.between(testStartDateTime, testEndDateTime).toMinutes() + ", " +
                testTariffCode;

        when(readerMock.readLine()).thenReturn(CDRPlusString);

        // Act
        Optional<CDRPlus> resultCDRPlus = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultCDRPlus.isPresent()).isTrue();
        assertThat(resultCDRPlus.get().getCallTypeCode()).isEqualTo(testCallTypeCode);
        assertThat(resultCDRPlus.get().getPhoneNumber()).isEqualTo(testPhoneNumber);
        assertThat(resultCDRPlus.get().getStartDateTime()).isEqualTo(testStartDateTime);
        assertThat(resultCDRPlus.get().getEndDateTime()).isEqualTo(testEndDateTime);
        assertThat(resultCDRPlus.get().getTariffCode()).isEqualTo(testTariffCode);

        verify(readerMock, times(1)).readLine();
    }

    @Test
    void testDeserializeShouldThrowParseException() throws IOException {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "73333333333";
        String testCallTypeCode = "02";
        String testTariffCode = "03";

        String CDRPlusString = testCallTypeCode + ", " +
                testPhoneNumber + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter) + ", " +
                Duration.between(testStartDateTime, testEndDateTime).toMinutes() + ", " +
                testTariffCode + ", EXTRA_INFORMATION";

        when(readerMock.readLine()).thenReturn(CDRPlusString);

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
        Optional<CDRPlus> resultCDR = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultCDR.isEmpty()).isTrue();

        verify(readerMock, times(1)).readLine();
    }
}
