package ru.trkpo.datagen.service.dateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.Pair;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class LocalDateTimeGeneratorTest {

    private static final LocalDateTime LOWER_BOUND = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime UPPER_BOUND = LocalDateTime.of(2024, 2, 1, 0, 0);
    private static final Duration MAX_DURATION = Duration.ofHours(1);
    private static final LocalDateTimeGenerator underTestGenerator = new LocalDateTimeGeneratorImpl();

    @BeforeAll
    static void setUpClass() {
        setField(underTestGenerator, "lowerBound", LOWER_BOUND);
        setField(underTestGenerator, "upperBound", UPPER_BOUND);
        setField(underTestGenerator, "maxDuration", MAX_DURATION);
    }

    @Test
    void testGenerateDateTimeShouldReturnDateTimeBetweenLowerAndUpperBound() {
        // Arrange
        // Act
        Pair<LocalDateTime, LocalDateTime> resultPair = underTestGenerator.generateDateTime();
        // Assert
        assertTrue(resultPair.getFirst().isAfter(LOWER_BOUND) && resultPair.getSecond().isBefore(UPPER_BOUND));
    }

    @Test
    void testGenerateDateTimeShouldReturnResultDurationLessThanMaxDuration() {
        // Arrange
        // Act
        Pair<LocalDateTime, LocalDateTime> resultPair = underTestGenerator.generateDateTime();
        Duration totalDuration = Duration.between(resultPair.getFirst(), resultPair.getSecond());
        // Assert
        assertThat(totalDuration).isLessThan(MAX_DURATION);
    }

    @Test
    void testUpdateDateTimeBoundsShouldIncreaseDateTimeBoundsByOneMonth() {
        // Arrange
        // Act
        underTestGenerator.updateDateTimeBoubds();
        LocalDateTime newLowerBound = (LocalDateTime) getField(underTestGenerator, "lowerBound");
        LocalDateTime newUpperBound = (LocalDateTime) getField(underTestGenerator, "upperBound");
        // Assert
        assertThat(newLowerBound).hasMonth(LOWER_BOUND.getMonth().plus(1));
        assertThat(newUpperBound).hasMonth(UPPER_BOUND.getMonth().plus(1));
    }
}
