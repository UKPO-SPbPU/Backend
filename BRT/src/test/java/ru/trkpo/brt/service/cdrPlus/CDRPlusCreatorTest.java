package ru.trkpo.brt.service.cdrPlus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.entity.Tariff;
import ru.trkpo.common.exception.NoDataFoundException;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CDRPlusCreatorTest {

    @Mock
    private PhoneNumberService phoneNumberServiceMock;

    @InjectMocks
    private CDRPlusCreatorImpl underTestCreator;

    @Test
    void testCreateRecordShouldReturnCDRPlusRecord() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "71112223344";
        String testCallTypeCode = "02";

        CDR testCDR = new CDR(testCallTypeCode,
                testPhoneNumber,
                testStartDateTime,
                testEndDateTime);

        Tariff testTariff = new Tariff("01", "Test", "Test", null, null);

        when(phoneNumberServiceMock.findActiveTariff(anyString())).thenReturn(testTariff);

        // Act
        Optional<CDRPlus> resultCDRPlus = underTestCreator.createRecord(testCDR);

        // Assert
        assertThat(resultCDRPlus.isPresent()).isTrue();
        assertThat(resultCDRPlus.get().getCallTypeCode()).isEqualTo(testCallTypeCode);
        assertThat(resultCDRPlus.get().getPhoneNumber()).isEqualTo(testPhoneNumber);
        assertThat(resultCDRPlus.get().getStartDateTime()).isEqualTo(testStartDateTime);
        assertThat(resultCDRPlus.get().getEndDateTime()).isEqualTo(testEndDateTime);
        assertThat(resultCDRPlus.get().getTariffCode()).isEqualTo(testTariff.getId());

        verify(phoneNumberServiceMock, times(1)).findActiveTariff(anyString());
    }

    @Test
    void testCreateRecordShouldThrowNoDataFoundExceptionWhenInvalidPhone() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "71112223344";
        String testCallTypeCode = "02";

        CDR testCDR = new CDR(testCallTypeCode,
                testPhoneNumber,
                testStartDateTime,
                testEndDateTime);

        when(phoneNumberServiceMock.findActiveTariff(anyString())).thenThrow(NoDataFoundException.class);

        // Act
        Optional<CDRPlus> resultCDRPlus = underTestCreator.createRecord(testCDR);

        // Assert
        assertThat(resultCDRPlus.isEmpty()).isTrue();

        verify(phoneNumberServiceMock, times(1)).findActiveTariff(anyString());
    }
}
