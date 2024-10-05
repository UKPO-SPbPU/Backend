package ru.trkpo.brt.service.tarifficationReport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.UrlResource;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class TarifficationReportProviderTest {

    @Mock
    private Deserializer<TarifficationReportDTO> reportDeserializerMock;

    @InjectMocks
    private TarrificationReportProviderImpl underTestProvider;
    private static final String FILE_PATH = "src/test/resources/test-tariffication-reports.txt";

    @BeforeEach
    void setUpClass() {
        setField(underTestProvider, "sourceURL", FILE_PATH);
    }

    @Test
    void testInitShouldMakeReader() throws IOException {
        // Arrange & Act
        try (MockedConstruction<UrlResource> mockUrlResource = mockConstruction(UrlResource.class, (mock, context) -> {
            when(mock.getInputStream()).thenReturn(new FileInputStream(FILE_PATH));
        })) {
            underTestProvider.init();
        }

        // Assert
        assertThat(getField(underTestProvider, "reader")).isNotNull();

        verify(reportDeserializerMock, never()).deserialize(any(BufferedReader.class));
    }

    @Test
    void testGetReportsShouldReturnListOfReports() throws IOException {
        // Arrange & Act
        String tariffCode = "02";
        String phoneNumber1 = "71112223344";
        String phoneNumber2 = "71112223345";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);
        String callTypeCode1 = "01";
        String callTypeCode2 = "02";
        BigDecimal cost = BigDecimal.valueOf(33);

        CallDataDTO cd1 = new CallDataDTO(
                callTypeCode1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                cost);

        CallDataDTO cd2 = new CallDataDTO(
                callTypeCode1,
                startDateTime2,
                endDateTime2,
                Duration.between(startDateTime2, endDateTime2),
                cost);

        CallDataDTO cd3 = new CallDataDTO(
                callTypeCode2,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                cost);

        CallDataDTO cd4 = new CallDataDTO(
                callTypeCode2,
                startDateTime2,
                endDateTime2,
                Duration.between(startDateTime2, endDateTime2),
                cost);

        List<CallDataDTO> calldata1 = new ArrayList<>();
        calldata1.add(cd1);
        calldata1.add(cd4);

        List<CallDataDTO> calldata2 = new ArrayList<>();
        calldata2.add(cd2);
        calldata2.add(cd3);

        long totalMinutes = Duration.between(startDateTime1, endDateTime1).toMinutes() +
                Duration.between(startDateTime2, endDateTime2).toMinutes();
        BigDecimal totalCost = cost.add(cost);

        TarifficationReportDTO report1 = new TarifficationReportDTO(
                phoneNumber1,
                tariffCode,
                calldata1,
                totalMinutes,
                totalCost);

        TarifficationReportDTO report2 = new TarifficationReportDTO(
                phoneNumber2,
                tariffCode,
                calldata2,
                totalMinutes,
                totalCost);

        List<TarifficationReportDTO> testList = new ArrayList<>();
        testList.add(report1);
        testList.add(report2);

        try (MockedConstruction<UrlResource> mockUrlResource = mockConstruction(UrlResource.class, (mock, context) -> {
            when(mock.getInputStream()).thenReturn(new FileInputStream(FILE_PATH));
        })) {
            underTestProvider.init();
        }
        when(reportDeserializerMock.deserialize(any(BufferedReader.class)))
                .thenReturn(Optional.of(report1))
                .thenReturn(Optional.of(report2))
                .thenReturn(Optional.empty());

        List<TarifficationReportDTO> resultReportList = underTestProvider.getReports();

        // Assert
        assertThat(resultReportList).isEqualTo(testList);

        verify(reportDeserializerMock, times(3)).deserialize(any(BufferedReader.class));
    }

    @Test
    void testGetReportsCorrectlyWorkWithUncheckedException() throws IOException {
        // Arrange &
        try (MockedConstruction<UrlResource> mockUrlResource = mockConstruction(UrlResource.class, (mock, context) -> {
            when(mock.getInputStream()).thenReturn(new FileInputStream(FILE_PATH));
        })) {
            underTestProvider.init();
        }
        when(reportDeserializerMock.deserialize(any(BufferedReader.class))).thenThrow(IOException.class);

        // Act & Assert
        assertThrows(UncheckedIOException.class, () -> {
            underTestProvider.getReports();
        });

        verify(reportDeserializerMock, times(1)).deserialize(any(BufferedReader.class));
    }
}
