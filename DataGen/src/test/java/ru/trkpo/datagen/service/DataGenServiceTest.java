package ru.trkpo.datagen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDR;
import ru.trkpo.datagen.service.cdr.CDRGeneratorImpl;
import ru.trkpo.datagen.service.cdr.CDRWriterImpl;
import ru.trkpo.datagen.service.dateTime.LocalDateTimeGeneratorImpl;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class DataGenServiceTest {

    @Mock
    private CDRGeneratorImpl cdrGeneratorMock;

    @Mock
    private LocalDateTimeGeneratorImpl localDateTimeGeneratorMock;

    @Mock
    private CDRWriterImpl cdrWriterMock;

    @InjectMocks
    private DataGenService underTestService;
    private static final int CDRs_COUNT = 10;
    private static final String CDR_FILE_PATH = "src/test/resources/files/cdr.txt";
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @BeforeEach
    void setUp() {
        setField(underTestService, "cdrsCount", CDRs_COUNT);
    }

    @Test
    void testGenerateCDRsShouldCreateFileWithSpecifiedCountOfRecords() throws IOException {
        // Arrange
        File cdrFile = new File(CDR_FILE_PATH);
        String callTypeCode = "01";
        String phoneNumber = "71112223344";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 10);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 1, 1, 0, 20);
        CDR testRecord = new CDR(callTypeCode, phoneNumber, startDateTime, endDateTime);
        // Act
        doAnswer(invocation -> {
            try (FileWriter writer = new FileWriter(CDR_FILE_PATH, false)) {
                writer.write("");
            }
            return null;
        }).when(cdrWriterMock).init();
        doAnswer(invocation -> {
            try (FileWriter writer = new FileWriter(CDR_FILE_PATH, true)) {
                CDR record = invocation.getArgument(0);
                String cdrString = record.getCallTypeCode() + ", "
                        + record.getPhoneNumber() + ", "
                        + record.getStartDateTime().format(formatter) + ", "
                        + record.getEndDateTime().format(formatter) + '\n';
                writer.write(cdrString);
            }
            return null;
        }).when(cdrWriterMock).write(any(CDR.class));
        when(cdrGeneratorMock.generateRecord()).thenReturn(testRecord);
        doNothing().when(localDateTimeGeneratorMock).updateDateTimeBoubds();
        underTestService.generateCDRs();
        // Assert
        assertThat(cdrFile).exists().isNotEmpty();
        try (BufferedReader reader = new BufferedReader(new FileReader(cdrFile))) {
            for (int i = 0; i < CDRs_COUNT; i++) {
                String line = reader.readLine();
                assertThat(line).isNotNull().isNotEmpty();
                assertThat(line.split(", ")).hasSize(4);
            }
            assertThat(reader.readLine()).isNull();
        }
        verify(cdrWriterMock, times(1)).init();
        verify(cdrGeneratorMock, times(CDRs_COUNT)).generateRecord();
        verify(cdrWriterMock, times(CDRs_COUNT)).write(any(CDR.class));
        verify(localDateTimeGeneratorMock, times(1)).updateDateTimeBoubds();
    }
}
