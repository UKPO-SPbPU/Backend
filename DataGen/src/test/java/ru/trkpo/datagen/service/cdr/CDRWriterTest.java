package ru.trkpo.datagen.service.cdr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.CDR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CDRWriterTest {

    @Mock
    private CDRSerializer serializerMock;

    @InjectMocks
    private CDRWriterImpl underTestWriter;
    
    private static final String CDR_FILE_PATH = "src/test/resources/files/cdr.txt";
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @BeforeEach
    void setUp() {
        setField(underTestWriter, "cdrFilePath", CDR_FILE_PATH);
    }

    @Test
    void testInitShouldCleanCDRFile() {
        // Arrange
        File cdrFile = new File(CDR_FILE_PATH);
        // Act
        // Assert
        assertDoesNotThrow(() -> underTestWriter.init());
        assertThat(cdrFile).exists().isEmpty();
        verify(serializerMock, never()).serialize(any(CDR.class));
    }

    @Test
    void testWriteShouldCDRRecord() throws FileNotFoundException {
        // Arrange
        File cdrFile = new File(CDR_FILE_PATH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        Scanner reader = new Scanner(new FileReader(cdrFile));
        String callTypeCode = "01";
        String phoneNumber = "71112223344";
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 1, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 1, 1, 1, 30);
        String resultCDRString = callTypeCode + ", "
                + phoneNumber + ", "
                + startDateTime.format(formatter) + ", "
                + endDateTime.format(formatter) + '\n';
        CDR record = new CDR(callTypeCode, phoneNumber, startDateTime, endDateTime);
        // Act
        when(serializerMock.serialize(Mockito.any(CDR.class))).thenReturn(resultCDRString);
        // Assert
        assertDoesNotThrow(() -> {
            underTestWriter.init();
            underTestWriter.write(record);
        });
        String result = reader.nextLine();
        assertThat(cdrFile).exists().isNotEmpty();
        assertThat(result.split(", ")).hasSize(4);
        assertThat(result).isEqualTo(resultCDRString.substring(0, resultCDRString.length() - 1));
        verify(serializerMock, times(1)).serialize(any(CDR.class));
    }
}
