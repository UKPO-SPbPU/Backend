package ru.trkpo.brt.service.cdr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CDRDeserializer implements Deserializer<CDR> {

    @Value("${brt-service.services.cdr.date-time-format}")
    private String dateTimeFormat;

    @Override
    public Optional<CDR> deserialize(BufferedReader reader) throws IOException {
        String stringCDR = reader.readLine();
        if (stringCDR == null)
            return Optional.empty();

        String[] split = stringCDR.split(", ");
        if (split.length != 4)
            throw new ParseException(stringCDR, 0, "Invalid cdr context");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        LocalDateTime startTime = LocalDateTime.parse(split[2], formatter);
        LocalDateTime endTime = LocalDateTime.parse(split[3], formatter);
        return Optional.of(new CDR(split[0], split[1], startTime, endTime));
    }
}
