package ru.trkpo.brt.service.tarifficationReport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.ParseException;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TarifficationReportDeserializerTest {

    @Mock
    private BufferedReader readerMock;

    @Mock
    private CallDataDeserializer callDataDeserializerMock;

    @InjectMocks
    private TarifficationReportDeserializer underTestDeserializer;

    @Test
    void testDeserializeShouldReturnTarifficationReport() throws IOException {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testCallTypeCode = "02";
        Duration testDuration = Duration.ofMinutes(44);
        BigDecimal testCost = BigDecimal.valueOf(33);

        CallDataDTO testCallData = new CallDataDTO(
                testCallTypeCode,
                testStartDateTime,
                testEndDateTime,
                testDuration,
                testCost);

        String testTariffCode = "02";
        String testPhoneNumber = "71112348878";
        int testTotalMinutes = 52;
        BigDecimal testTotalCost = BigDecimal.valueOf(Double.parseDouble("122"));
        int testCallDataCount = 1;

        String testDataString = testTariffCode + ", " +
                testPhoneNumber + ", " +
                testTotalMinutes + ", " +
                testTotalCost + ", " +
                testCallDataCount;

        when(readerMock.readLine()).thenReturn(testDataString);
        when(callDataDeserializerMock.deserialize(readerMock)).thenReturn(Optional.of(testCallData));

        // Act
        Optional<TarifficationReportDTO> resultReport = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultReport.isPresent()).isTrue();
        assertThat(resultReport.get().getTariffCode()).isEqualTo(testTariffCode);
        assertThat(resultReport.get().getTotalCost()).isEqualTo(testTotalCost);
        assertThat(resultReport.get().getPhoneNumber()).isEqualTo(testPhoneNumber);
        assertThat(resultReport.get().getTotalMinutes()).isEqualTo(testTotalMinutes);
        assertThat(resultReport.get().getCallsList().size()).isEqualTo(testCallDataCount);
        assertThat(resultReport.get().getCallsList().get(0)).isEqualTo(testCallData);

        verify(readerMock, times(1)).readLine();
        verify(callDataDeserializerMock, times(1)).deserialize(readerMock);
    }

    @Test
    void testDeserializeShouldThrowParseExceptionWhenCallDataIsEmpty() throws IOException {
        // Arrange
        String testTariffCode = "02";
        String testPhoneNumber = "71112348878";
        int testTotalMinutes = 52;
        BigDecimal testTotalCost = BigDecimal.valueOf(122);
        int testCallDataCount = 1;

        String testDataString = testTariffCode + ", " +
                testPhoneNumber + ", " +
                testTotalMinutes + ", " +
                testTotalCost + ", " +
                testCallDataCount;

        when(readerMock.readLine()).thenReturn(testDataString);
        when(callDataDeserializerMock.deserialize(readerMock)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParseException.class, () -> {
            underTestDeserializer.deserialize(readerMock);
        });

        verify(readerMock, times(1)).readLine();
        verify(callDataDeserializerMock, times(1)).deserialize(any(BufferedReader.class));
    }

    @Test
    void testDeserializeShouldThrowParseException() throws IOException {
        // Arrange
        String dataString = "";

        when(readerMock.readLine()).thenReturn(dataString);

        // Act & Assert
        assertThrows(ParseException.class, () -> {
            underTestDeserializer.deserialize(readerMock);
        });

        verify(readerMock, times(1)).readLine();
        verify(callDataDeserializerMock, never()).deserialize(any(BufferedReader.class));
    }

    @Test
    void testDeserializeShouldReturnEmptyOptional() throws IOException {
        // Arrange
        when(readerMock.readLine()).thenReturn(null);

        // Act
        Optional<TarifficationReportDTO> resultTarifficationReport = underTestDeserializer.deserialize(readerMock);

        // Assert
        assertThat(resultTarifficationReport.isEmpty()).isTrue();

        verify(readerMock, times(1)).readLine();
        verify(callDataDeserializerMock, never()).deserialize(any(BufferedReader.class));
    }
}
