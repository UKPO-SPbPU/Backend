package ru.trkpo.hrs.service.CallData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.dto.CallDataDTO;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CallDataSerializerTest {

    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    private static final CallDataSerializer underTestSerializer = new CallDataSerializer();

    @BeforeAll
    static void setUp() {
        setField(underTestSerializer, "dateTimeFormat", DATE_TIME_FORMAT);
    }

    @Test
    public void testSerializeShouldReturnCallDataString() {
        // Arrange
        String testCallTypeCode = "02";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime testStartDateTime = LocalDateTime.parse("2024/01/06 23:04:51", formatter);
        LocalDateTime testEndDateTime = LocalDateTime.parse("2024/01/06 23:49:07", formatter);
        Duration testDuration = Duration.between(testStartDateTime, testEndDateTime);
        BigDecimal testCost = BigDecimal.valueOf(18);

        CallDataDTO testCallData = new CallDataDTO(
                testCallTypeCode,
                testStartDateTime,
                testEndDateTime,
                testDuration,
                testCost);

        String testString = testCallTypeCode + ", " +
                testStartDateTime.format(formatter) + ", " +
                testEndDateTime.format(formatter) + ", " +
                Duration.between(testStartDateTime, testEndDateTime).toMinutes() + ", " +
                testCost + "\n";

        // Act
        String resultString = underTestSerializer.serialize(testCallData);

        // Assert
        assertThat(resultString).isEqualTo(testString);
    }
}
