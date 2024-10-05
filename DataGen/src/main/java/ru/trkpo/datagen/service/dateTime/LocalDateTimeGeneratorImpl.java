package ru.trkpo.datagen.service.dateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Service
public class LocalDateTimeGeneratorImpl implements LocalDateTimeGenerator {

    @Value("${data-gen-service.services.date-time-generator.lower-bound}")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lowerBound;

    @Value("${data-gen-service.services.date-time-generator.upper-bound}")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime upperBound;

    @Value("${data-gen-service.services.date-time-generator.max-duration}")
    private Duration maxDuration;

    private final Random random = new Random();

    @Override
    public Pair<LocalDateTime, LocalDateTime> generateDateTime() {
        long lowerB = lowerBound.toEpochSecond(ZoneOffset.UTC);
        long upperB = upperBound.toEpochSecond(ZoneOffset.UTC);

        long startSeconds = random.nextLong(lowerB, upperB);
        long endSeconds = random.nextLong(startSeconds, Math.min(startSeconds + maxDuration.toSeconds(), upperB));
        LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(startSeconds, 0, ZoneOffset.UTC);
        LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(endSeconds, 0, ZoneOffset.UTC);
        return new Pair<>(startDateTime, endDateTime);
    }

    @Override
    public void updateDateTimeBoubds() {
        lowerBound = lowerBound.plusMonths(1);
        upperBound = upperBound.plusMonths(1);
    }
}
