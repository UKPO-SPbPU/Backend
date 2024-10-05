package ru.trkpo.hrs.service.tarifficationReport;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Serializer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TarifficationReportSerializerTest {

    @Mock
    private Serializer<CallDataDTO> callDataSerializerMock;

    @InjectMocks
    private TarifficationReportSerializer underTestSerializer;

    @Test
    void testSerializeShouldReturnStringReport() {
        // Arrange
        String tariffCode = "03";
        String phoneNumber = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);
        String callTypeCode1 = "01";
        String callTypeCode2 = "02";
        BigDecimal cost = BigDecimal.valueOf(Double.parseDouble("33"));

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
                cost;

        String testStringPart3 = callTypeCode1 + ", " +
                startDateTime2.format(formatter) + ", " +
                endDateTime2.format(formatter) + ", " +
                Duration.between(startDateTime1, endDateTime1).toMinutes() + ", " +
                cost;

        String testString = testStringPart1.concat(testStringPart2).concat(testStringPart3);

        when(callDataSerializerMock.serialize(any(CallDataDTO.class)))
                .thenReturn(testStringPart2)
                .thenReturn(testStringPart3);

        // Act
        String resultString = underTestSerializer.serialize(report);

        // Assert
        assertThat(resultString).isEqualTo(testString);

        verify(callDataSerializerMock, times(2)).serialize(any(CallDataDTO.class));
    }
}
