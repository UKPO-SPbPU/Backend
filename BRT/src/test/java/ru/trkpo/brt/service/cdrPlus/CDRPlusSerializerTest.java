package ru.trkpo.brt.service.cdrPlus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.CDRPlus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CDRPlusSerializerTest {
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final CDRPlusSerializer underTestSerializer = new CDRPlusSerializer();

    @BeforeAll
    static void setUpClass() {
        setField(underTestSerializer, "dateTimeFormat", DATE_TIME_FORMAT);
    }

    @Test
    void testSerializeShouldReturnCDRPlusString() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        String testPhoneNumber = "71112223344";
        String testCallTypeCode = "02";
        Duration testDuration = Duration.ofMinutes(44);
        String testTariffCode = "03";

        CDRPlus testCDRPlus = new CDRPlus(
                testCallTypeCode,
                testPhoneNumber,
                testStartDateTime,
                testEndDateTime,
                testDuration,
                testTariffCode);

        String testCDRPlusString = testCallTypeCode + ", " +
                testPhoneNumber + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter) + ", " +
                testDuration.toMinutes() + ", " +
                testTariffCode + "\n";

        // Act
        String resultString = underTestSerializer.serialize(testCDRPlus);

        // Assert
        assertThat(resultString).isNotNull().isNotEmpty().isEqualTo(testCDRPlusString);
    }
}
