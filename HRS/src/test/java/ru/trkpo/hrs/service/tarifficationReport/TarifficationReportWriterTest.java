package ru.trkpo.hrs.service.tarifficationReport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class TarifficationReportWriterTest {

    @Mock
    private Serializer<TarifficationReportDTO> reportSerializerMock;

    @InjectMocks
    private TarifficationReportWriterImpl underTestWriter;

    private static final String REPORT_FILE_PATH = "../files/test-tariffication-reports.txt";

    @BeforeEach
    void setUp() {
        setField(underTestWriter, "reportFilePath", REPORT_FILE_PATH);
    }

    @Test
    void testInitShouldCleanReportFile() {
        // Arrange
        File reportFile = new File(REPORT_FILE_PATH);

        // Act & Assert
        assertDoesNotThrow(() -> underTestWriter.init());
        assertThat(reportFile).exists().isEmpty();

        verify(reportSerializerMock, never()).serialize(any(TarifficationReportDTO.class));
    }

    @Test
    void testWriteShouldCDRPlusRecord() throws FileNotFoundException {
        // Arrange
        File reportFile = new File(REPORT_FILE_PATH);
        Scanner reader = new Scanner(new FileReader(reportFile));

        String tariffCode = "03";
        String phoneNumber = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);
        String callTypeCode1 = "01";
        String callTypeCode2 = "02";
        BigDecimal cost = BigDecimal.valueOf(33);

        CallDataDTO cd1 = new CallDataDTO(callTypeCode1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                cost);

        CallDataDTO cd2 = new CallDataDTO(callTypeCode2,
                startDateTime2,
                endDateTime2,
                Duration.between(startDateTime2, endDateTime2),
                cost);

        List<CallDataDTO> calldataList = new ArrayList<>();
        calldataList.add(cd1);
        calldataList.add(cd2);

        long totalMinutes = Duration.between(startDateTime1, endDateTime1).toMinutes() +
                Duration.between(startDateTime2, endDateTime2).toMinutes();
        BigDecimal totalCost = cost.add(cost);
        TarifficationReportDTO report = new TarifficationReportDTO(phoneNumber,
                tariffCode,
                calldataList,
                totalMinutes,
                totalCost);

        String testStringPart1 = tariffCode + ", " +
                phoneNumber + ", " +
                totalMinutes + ", " +
                totalCost + ", " +
                calldataList.size() + "\n";

        String testStringPart2 = callTypeCode1 + ", " +
                startDateTime1.format(formatter) + ", " +
                endDateTime1.format(formatter) + ", " +
                Duration.between(startDateTime1, endDateTime1).toMinutes() + ", " +
                cost + "\n";

        String testStringPart3 = callTypeCode2 + ", " +
                startDateTime2.format(formatter) + ", " +
                endDateTime2.format(formatter) + ", " +
                Duration.between(startDateTime1, endDateTime1).toMinutes() + ", " +
                cost + "\n";

        String testString = testStringPart1.concat(testStringPart2).concat(testStringPart3);

        when(reportSerializerMock.serialize(any(TarifficationReportDTO.class))).thenReturn(testString);

        // Act & Assert
        assertDoesNotThrow(() -> {
            underTestWriter.init();
            underTestWriter.write(report);
        });

        assertThat(reportFile).exists().isNotEmpty();

        String result = reader.nextLine();
        assertThat(result).isEqualTo(testStringPart1.substring(0, testStringPart1.length() - 1));

        result = reader.nextLine();
        assertThat(result).isEqualTo(testStringPart2.substring(0, testStringPart2.length() - 1));

        result = reader.nextLine();
        assertThat(result).isEqualTo(testStringPart3.substring(0, testStringPart3.length() - 1));

        verify(reportSerializerMock, times(1)).serialize(any(TarifficationReportDTO.class));
    }
}
