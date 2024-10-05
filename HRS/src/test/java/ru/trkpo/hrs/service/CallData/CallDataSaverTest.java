package ru.trkpo.hrs.service.CallData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.entity.CallHistory;
import ru.trkpo.common.data.entity.CallType;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.service.callHistory.CallHistoryService;
import ru.trkpo.common.service.callType.CallTypeService;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.postgresql.core.Parser.parseLong;

@ExtendWith(MockitoExtension.class)
public class CallDataSaverTest {

    @Mock
    private CallHistoryService callHistoryServiceMock;
    @Mock
    private PhoneNumberService phoneNumberServiceMock;
    @Mock
    private CallTypeService callTypeServiceMock;

    @InjectMocks
    private CallDataSaver underTestSaver;

    @Test
    public void testSaveCallShouldNotThrowAnyException() {
        // Arrange
        String testPhoneNumber = "79112348975";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        BigDecimal testCost = BigDecimal.valueOf(18);
        PhoneNumber testPhone = new PhoneNumber(parseLong("212", 0, 2),null,
                testPhoneNumber,BigDecimal.valueOf(123), null,null);
        CallType testCalltype = new CallType("02", "02", null, null);
        CallDataDTO callData = new CallDataDTO(
                "02",
                testStartDateTime,
                testEndDateTime,
                Duration.between(testStartDateTime, testEndDateTime),
                testCost);

        when(phoneNumberServiceMock.findByPhoneNumber(anyString())).thenReturn(testPhone);
        when(callTypeServiceMock.findByCallTypeCode(anyString())).thenReturn(testCalltype);
        doNothing().when(callHistoryServiceMock).saveCall(any(CallHistory.class));

        // Act & Assert
        assertDoesNotThrow(() -> {
            underTestSaver.saveCall(testPhoneNumber, callData);
        });

        verify(phoneNumberServiceMock, times(1)).findByPhoneNumber(anyString());
        verify(callTypeServiceMock, times(1)).findByCallTypeCode(anyString());
        verify(callHistoryServiceMock, times(1)).saveCall(any(CallHistory.class));
    }
}
