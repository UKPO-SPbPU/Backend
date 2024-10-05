package ru.trkpo.datagen.service.cdr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.CDR;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class CDRSerializerTest {

    private static final CDRSerializer underTestSrlzr = new CDRSerializer();
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @BeforeAll
    static void setUpClass() {
        setField(underTestSrlzr, "dateTimeFormat", DATE_TIME_FORMAT);
    }

    @Test
    void testSerializeShouldReturnCDRString() {
        // Arrange
        String testCDRString = "02, 71112223344, 2024/01/06 23:04:51, 2024/01/06 23:49:07\n";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime startDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        CDR testCDR = new CDR("02", "71112223344", startDateTime, endDateTime);
        // Act
        String resultString = underTestSrlzr.serialize(testCDR);
        // Assert
        assertThat(resultString).isNotNull().isNotEmpty().isEqualTo(testCDRString);
    }
}
