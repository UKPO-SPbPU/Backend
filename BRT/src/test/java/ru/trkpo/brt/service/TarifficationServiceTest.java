package ru.trkpo.brt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.brt.messageBroker.TarifficationRequestMessanger;
import ru.trkpo.brt.service.cdr.CDRProvider;
import ru.trkpo.brt.service.cdrPlus.CDRPlusCreator;
import ru.trkpo.brt.service.cdrPlus.CDRPlusWriter;
import ru.trkpo.brt.service.tarifficationReport.TarifficationReportProvider;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.messageBroker.ResponseStatus;
import ru.trkpo.common.messageBroker.ServiceResponse;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TarifficationServiceTest {

    @Mock
    private CDRProvider cdrProviderMock;

    @Mock
    private TarifficationReportProvider reportProviderMock;

    @Mock
    private CDRPlusCreator cdrPlusCreatorMock;

    @Mock
    private CDRPlusWriter cdrPlusWriterMock;

    @Mock
    private TarifficationRequestMessanger tarifficationRequestMessangerMock;

    @Mock
    private PhoneNumberService phoneNumberServiceMock;

    @InjectMocks
    private TarifficationService underTestService;

    @Test
    void testTarifficateShouldReturnSuccessfulResponse() throws IOException {
        // Arrange
        String callTypeCode2 = "02";
        String phoneNumber1 = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);

        CDR testCDR1 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1);

        CDR testCDR2 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime2,
                endDateTime2);

        List<CDR> testCDRList = new ArrayList<>();
        testCDRList.add(testCDR1);
        testCDRList.add(testCDR2);

        ServiceResponse testResponse = new ServiceResponse(ResponseStatus.SUCCESS, "OK");

        String tariffCode = "02";

        CDRPlus testCDRPlus = new CDRPlus(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                tariffCode);

        String phoneNumber2 = "71112223345";
        String callTypeCode1 = "01";
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

        long totalMinutes = Duration.between(startDateTime1, endDateTime1).toMinutes()
                + Duration.between(startDateTime2, endDateTime2).toMinutes();
        BigDecimal totalCost = BigDecimal.valueOf(66);

        TarifficationReportDTO testReport1 = new TarifficationReportDTO(
                phoneNumber1,
                tariffCode,
                calldata1,
                totalMinutes,
                totalCost);

        TarifficationReportDTO testReport2 = new TarifficationReportDTO(
                phoneNumber2,
                tariffCode,
                calldata2,
                totalMinutes,
                totalCost);

        List<TarifficationReportDTO> testReportsList = new ArrayList<>();
        testReportsList.add(testReport1);
        testReportsList.add(testReport2);

        PhoneNumber testPhoneNumber1 = new PhoneNumber();
        PhoneNumber testPhoneNumber2 = new PhoneNumber();

        testPhoneNumber1.setBalance(BigDecimal.valueOf(300));
        testPhoneNumber1.setPhoneNumber(phoneNumber1);

        testPhoneNumber2.setBalance(BigDecimal.valueOf(300));
        testPhoneNumber2.setPhoneNumber(phoneNumber2);

        doNothing().when(cdrProviderMock).init();
        doNothing().when(cdrPlusWriterMock).init();
        when(cdrProviderMock.getCDRsList()).thenReturn(testCDRList);
        when(cdrPlusCreatorMock.createRecord(any(CDR.class))).thenReturn(Optional.of(testCDRPlus));
        doNothing().when(cdrPlusWriterMock).write(any(CDRPlus.class));
        when(tarifficationRequestMessangerMock.requestTariffication()).thenReturn(testResponse);
        doNothing().when(reportProviderMock).init();
        when(reportProviderMock.getReports()).thenReturn(testReportsList);
        when(phoneNumberServiceMock.findByPhoneNumber(any(String.class)))
                .thenReturn(testPhoneNumber1)
                .thenReturn(testPhoneNumber2);
        when(phoneNumberServiceMock.save(any(PhoneNumber.class)))
                .thenReturn(testPhoneNumber1)
                .thenReturn(testPhoneNumber2);

        // Act
        ServiceResponse resultResponse = underTestService.tarifficate();

        // Assert
        assertThat(resultResponse).isEqualTo(testResponse);

        verify(cdrProviderMock, times(1)).init();
        verify(cdrPlusWriterMock, times(1)).init();
        verify(cdrProviderMock, times(1)).getCDRsList();
        verify(cdrPlusCreatorMock, times(2)).createRecord(any(CDR.class));
        verify(cdrPlusWriterMock, times(2)).write(any(CDRPlus.class));
        verify(tarifficationRequestMessangerMock, times(1)).requestTariffication();
        verify(reportProviderMock, times(1)).init();
        verify(reportProviderMock, times(1)).getReports();
        verify(phoneNumberServiceMock, times(2)).findByPhoneNumber(anyString());
        verify(phoneNumberServiceMock, times(2)).save(any(PhoneNumber.class));
    }

    @Test
    void testTarifficateShouldReturnConsumerErrorResponseWhenIOExceptionWhileInitCDRAndSDRPlusProviders() throws IOException {
        // Arrange
        ServiceResponse testResponse = new ServiceResponse(ResponseStatus.CONSUMER_ERROR, "Consumer Error!");
        IOException exception = new IOException("Consumer Error!");

        doNothing().when(cdrProviderMock).init();
        doThrow(exception).when(cdrPlusWriterMock).init();

        // Act
        ServiceResponse resultResponse = underTestService.tarifficate();

        // Assert
        assertThat(resultResponse.getResponseStatus()).isEqualTo(testResponse.getResponseStatus());
        assertThat(resultResponse.getMessage()).isEqualTo(testResponse.getMessage());

        verify(cdrProviderMock, times(1)).init();
        verify(cdrPlusWriterMock, times(1)).init();
        verify(cdrProviderMock, never()).getCDRsList();
        verify(cdrPlusCreatorMock, never()).createRecord(any(CDR.class));
        verify(cdrPlusWriterMock, never()).write(any(CDRPlus.class));
        verify(tarifficationRequestMessangerMock, never()).requestTariffication();
        verify(reportProviderMock, never()).init();
        verify(reportProviderMock, never()).getReports();
        verify(phoneNumberServiceMock, never()).findByPhoneNumber(anyString());
        verify(phoneNumberServiceMock, never()).save(any(PhoneNumber.class));
    }

    @Test
    void testTarifficateShouldThrowRuntimeExceptionWhenWritingThrowsIOException() throws IOException {
        // Arrange
        String callTypeCode2 = "02";
        String phoneNumber1 = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);

        CDR testCDR1 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1);

        CDR testCDR2 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime2,
                endDateTime2);

        List<CDR> testCDRList = new ArrayList<>();
        testCDRList.add(testCDR1);
        testCDRList.add(testCDR2);

        String tariffCode = "02";

        CDRPlus testCDRPlus = new CDRPlus(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                tariffCode);

        IOException exception = new IOException("Consumer Error!");

        doNothing().when(cdrProviderMock).init();
        doNothing().when(cdrPlusWriterMock).init();
        when(cdrProviderMock.getCDRsList()).thenReturn(testCDRList);
        when(cdrPlusCreatorMock.createRecord(any(CDR.class))).thenReturn(Optional.of(testCDRPlus));
        doThrow(exception).when(cdrPlusWriterMock).write(any(CDRPlus.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> underTestService.tarifficate());

        verify(cdrProviderMock, times(1)).init();
        verify(cdrPlusWriterMock, times(1)).init();
        verify(cdrProviderMock, times(1)).getCDRsList();
        verify(cdrPlusCreatorMock, times(1)).createRecord(any(CDR.class));
        verify(cdrPlusWriterMock, times(1)).write(any(CDRPlus.class));
        verify(tarifficationRequestMessangerMock, never()).requestTariffication();
        verify(reportProviderMock, never()).init();
        verify(reportProviderMock, never()).getReports();
        verify(phoneNumberServiceMock, never()).findByPhoneNumber(anyString());
        verify(phoneNumberServiceMock, never()).save(any(PhoneNumber.class));
    }

    @Test
    void testTarifficateShouldReturnConsumerErrorResponseWhenIOExceptionWhileInitReportProvider() throws IOException {
        // Arrange
        String callTypeCode2 = "02";
        String phoneNumber1 = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);

        CDR testCDR1 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1);

        CDR testCDR2 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime2,
                endDateTime2);

        List<CDR> testCDRList = new ArrayList<>();
        testCDRList.add(testCDR1);
        testCDRList.add(testCDR2);

        String tariffCode = "02";

        CDRPlus testCDRPlus = new CDRPlus(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                tariffCode);

        ServiceResponse testResponse = new ServiceResponse(ResponseStatus.SUCCESS, "OK");
        ServiceResponse testResultResponse = new ServiceResponse(ResponseStatus.CONSUMER_ERROR, "Consumer Error!");
        IOException exception = new IOException("Consumer Error!");

        doNothing().when(cdrProviderMock).init();
        doNothing().when(cdrPlusWriterMock).init();
        when(cdrProviderMock.getCDRsList()).thenReturn(testCDRList);
        when(cdrPlusCreatorMock.createRecord(any(CDR.class))).thenReturn(Optional.of(testCDRPlus));
        doNothing().when(cdrPlusWriterMock).write(any(CDRPlus.class));
        when(tarifficationRequestMessangerMock.requestTariffication()).thenReturn(testResponse);
        doThrow(exception).when(reportProviderMock).init();

        // Act
        ServiceResponse resultResponse = underTestService.tarifficate();

        // Assert
        assertThat(resultResponse.getResponseStatus()).isEqualTo(testResultResponse.getResponseStatus());
        assertThat(resultResponse.getMessage()).isEqualTo(testResultResponse.getMessage());

        verify(cdrProviderMock, times(1)).init();
        verify(cdrPlusWriterMock, times(1)).init();
        verify(cdrProviderMock, times(1)).getCDRsList();
        verify(cdrPlusCreatorMock, times(2)).createRecord(any(CDR.class));
        verify(cdrPlusWriterMock, times(2)).write(any(CDRPlus.class));
        verify(tarifficationRequestMessangerMock, times(1)).requestTariffication();
        verify(reportProviderMock, times(1)).init();
        verify(reportProviderMock, never()).getReports();
        verify(phoneNumberServiceMock, never()).findByPhoneNumber(anyString());
        verify(phoneNumberServiceMock, never()).save(any(PhoneNumber.class));
    }

    @Test
    void testTarifficateShouldReturnAnotherResponse() throws IOException {
        // Arrange
        // Arrange
        String callTypeCode2 = "02";
        String phoneNumber1 = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime startDateTime1 = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse("2024/01/06 23:50:53", formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse("2024/01/06 23:57:02", formatter);

        CDR testCDR1 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1);

        CDR testCDR2 = new CDR(
                callTypeCode2,
                phoneNumber1,
                startDateTime2,
                endDateTime2);

        List<CDR> testCDRList = new ArrayList<>();
        testCDRList.add(testCDR1);
        testCDRList.add(testCDR2);

        String tariffCode = "02";

        CDRPlus testCDRPlus = new CDRPlus(
                callTypeCode2,
                phoneNumber1,
                startDateTime1,
                endDateTime1,
                Duration.between(startDateTime1, endDateTime1),
                tariffCode);

        ServiceResponse testResponse = new ServiceResponse(ResponseStatus.CONSUMER_ERROR, "ERROR");

        doNothing().when(cdrProviderMock).init();
        doNothing().when(cdrPlusWriterMock).init();
        when(cdrProviderMock.getCDRsList()).thenReturn(testCDRList);
        when(cdrPlusCreatorMock.createRecord(any(CDR.class))).thenReturn(Optional.of(testCDRPlus));
        doNothing().when(cdrPlusWriterMock).write(any(CDRPlus.class));
        when(tarifficationRequestMessangerMock.requestTariffication()).thenReturn(testResponse);

        // Act
        ServiceResponse resultResponse = underTestService.tarifficate();

        // Assert
        assertThat(resultResponse).isEqualTo(testResponse);

        verify(cdrProviderMock, times(1)).init();
        verify(cdrPlusWriterMock, times(1)).init();
        verify(cdrProviderMock, times(1)).getCDRsList();
        verify(cdrPlusCreatorMock, times(2)).createRecord(any(CDR.class));
        verify(cdrPlusWriterMock, times(2)).write(any(CDRPlus.class));
        verify(tarifficationRequestMessangerMock, times(1)).requestTariffication();
        verify(reportProviderMock, never()).init();
        verify(reportProviderMock, never()).getReports();
        verify(phoneNumberServiceMock, never()).findByPhoneNumber(anyString());
        verify(phoneNumberServiceMock, never()).save(any(PhoneNumber.class));
    }
}
