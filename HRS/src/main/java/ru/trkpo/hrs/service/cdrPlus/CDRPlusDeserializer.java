package ru.trkpo.hrs.service.cdrPlus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CDRPlusDeserializer implements Deserializer<CDRPlus> {

    @Value("${hrs-service.services.cdr-plus.date-time-format}")
    private String dateTimeFormat;

    @Override
    public Optional<CDRPlus> deserialize(BufferedReader reader) throws IOException {
        String cdrPlusRecord = reader.readLine();
        if (cdrPlusRecord == null)
            return Optional.empty();

        String[] split = cdrPlusRecord.split(", ");
        if (split.length != 6)
            throw new ParseException(cdrPlusRecord, 0, "CDR PLus parse exception");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        LocalDateTime startTime = LocalDateTime.parse(split[2], formatter);
        LocalDateTime endTime = LocalDateTime.parse(split[3], formatter);
        return Optional.of(new CDRPlus(
                split[0],
                split[1],
                startTime,
                endTime,
                Duration.between(startTime, endTime),
                split[5]
        ));
    }
}
