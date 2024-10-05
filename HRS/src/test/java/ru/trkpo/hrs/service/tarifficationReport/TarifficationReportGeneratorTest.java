package ru.trkpo.hrs.service.tarifficationReport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.tariff.TariffService;
import ru.trkpo.hrs.service.CallData.CallDataSaver;
import ru.trkpo.hrs.service.cdrPlus.CDRPlusProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TarifficationReportGeneratorTest {

    @Mock
    private CDRPlusProvider cdrPlusProviderMock;
    @Mock
    private TarifficationReportWriter reportWriterMock;
    @Mock
    private TariffService tariffServiceMock;
    @Mock
    private CallDataSaver callDataSaverMock;

    @InjectMocks
    private TarifficationReportGeneratorImpl underTestGenerator;

    @Test
    void testGenerateReportsShouldNotThrowAnyException() throws IOException {
        // Arrange
        String callTypeCode = "02";
        String phoneNumber = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);
        String tariffCode = "03";

        CDRPlus record1 = new CDRPlus(callTypeCode,
                phoneNumber,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                tariffCode);

        CDRPlus record2 = new CDRPlus(callTypeCode,
                phoneNumber,
                startDateTime2,
                endDateTime2,
                Duration.between(startDateTime2, endDateTime2),
                tariffCode);

        List<CDRPlus> testCDRPlusList = new ArrayList<>();
        testCDRPlusList.add(record1);
        testCDRPlusList.add(record2);

        doNothing().when(cdrPlusProviderMock).init();
        doNothing().when(reportWriterMock).init();
        when(cdrPlusProviderMock.getCDRPlus()).thenReturn(testCDRPlusList);
        when(tariffServiceMock.applyTariff(any(CDRPlus.class))).thenReturn(BigDecimal.valueOf(123));
        doNothing().when(callDataSaverMock).saveCall(anyString(), any(CallDataDTO.class));
        doNothing().when(reportWriterMock).write(any(TarifficationReportDTO.class));

        // Act & Assert
        assertDoesNotThrow(() -> {
            underTestGenerator.generateReports();
        });

        verify(cdrPlusProviderMock, times(1)).init();
        verify(reportWriterMock, times(1)).init();
        verify(cdrPlusProviderMock, times(1)).getCDRPlus();
        verify(tariffServiceMock, times(2)).applyTariff(any(CDRPlus.class));
        verify(callDataSaverMock, times(2)).saveCall(anyString(), any(CallDataDTO.class));
        verify(reportWriterMock, times(1)).write(any(TarifficationReportDTO.class));
    }
}
