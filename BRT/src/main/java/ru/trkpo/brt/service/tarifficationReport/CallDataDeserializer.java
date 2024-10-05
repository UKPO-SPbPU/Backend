package ru.trkpo.brt.service.tarifficationReport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CallDataDeserializer implements Deserializer<CallDataDTO> {

    @Value("${brt-service.services.cdr.date-time-format}")
    private String dateTimeFormat;

    @Override
    public Optional<CallDataDTO> deserialize(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null)
            return Optional.empty();

        String[] split = line.split(", ");
        if (split.length != 5)
            throw new ParseException(line, 0, "Call data parse exception");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        LocalDateTime startTime = LocalDateTime.parse(split[1], formatter);
        LocalDateTime endTime = LocalDateTime.parse(split[2], formatter);
        Duration duration = Duration.ofMinutes(Long.parseLong(split[3]));
        BigDecimal cost = BigDecimal.valueOf(Double.parseDouble(split[4]));
        return Optional.of(new CallDataDTO(split[0], startTime, endTime, duration, cost));
    }
}
