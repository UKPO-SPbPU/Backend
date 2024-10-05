package ru.trkpo.datagen.service.dateTime;

import ru.trkpo.common.data.Pair;

import java.time.LocalDateTime;

public interface LocalDateTimeGenerator {
    Pair<LocalDateTime, LocalDateTime> generateDateTime();
    void updateDateTimeBoubds();
}
