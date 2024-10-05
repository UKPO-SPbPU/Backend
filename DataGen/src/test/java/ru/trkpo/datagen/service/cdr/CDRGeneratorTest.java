package ru.trkpo.datagen.service.cdr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.data.Pair;
import ru.trkpo.datagen.service.callType.CallTypeGeneratorImpl;
import ru.trkpo.datagen.service.dateTime.LocalDateTimeGeneratorImpl;
import ru.trkpo.datagen.service.phoneNumber.PhoneNumberGeneratorImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CDRGeneratorTest {

    @Mock
    private CallTypeGeneratorImpl callTypeGeneratorMock;

    @Mock
    private PhoneNumberGeneratorImpl phoneNumberGeneratorMock;

    @Mock
    private LocalDateTimeGeneratorImpl localDateTimeGeneratorMock;

    @InjectMocks
    private CDRGeneratorImpl underTestGenerator;

    @Test
    void testGenerateRecordShouldReturnNonEmptyCDR() {
        // Arrange
        String callTypeCode = "01";
        String phoneNumber = "71112223344";
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 1, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 1, 1, 1, 30);
        Pair<LocalDateTime, LocalDateTime> ldtPair = new Pair<>(startDateTime, endDateTime);
        // Act
        when(localDateTimeGeneratorMock.generateDateTime()).thenReturn(ldtPair);
        when(callTypeGeneratorMock.generateCallType()).thenReturn(callTypeCode);
        when(phoneNumberGeneratorMock.generateNumber()).thenReturn(phoneNumber);
        CDR resultCDR = underTestGenerator.generateRecord();
        // Assert
        assertThat(resultCDR).isNotNull();
        assertThat(resultCDR.getCallTypeCode()).isNotNull().isNotEmpty().isEqualTo(callTypeCode);
        assertThat(resultCDR.getPhoneNumber()).isNotNull().isNotEmpty().isEqualTo(phoneNumber);
        assertThat(resultCDR.getStartDateTime()).isNotNull().isEqualTo(startDateTime);
        assertThat(resultCDR.getEndDateTime()).isNotNull().isEqualTo(endDateTime).isAfter(startDateTime);
        verify(localDateTimeGeneratorMock, times(1)).generateDateTime();
        verify(callTypeGeneratorMock, times(1)).generateCallType();
        verify(phoneNumberGeneratorMock, times(1)).generateNumber();
    }
}
