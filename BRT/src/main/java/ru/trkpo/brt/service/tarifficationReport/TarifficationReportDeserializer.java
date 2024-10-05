package ru.trkpo.brt.service.tarifficationReport;

import lombok.AllArgsConstructor;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TarifficationReportDeserializer implements Deserializer<TarifficationReportDTO> {

    private final Deserializer<CallDataDTO> callDataDeserializer;

    @Override
    public Optional<TarifficationReportDTO> deserialize(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null)
            return Optional.empty();

        String[] split = line.split(", ");
        if (split.length != 5)
            throw new ParseException(line, 0, "Tariffication report parse exception");

        int callDataCount = Integer.parseInt(split[4]);
        BigDecimal cost = BigDecimal.valueOf(Double.parseDouble(split[3]));
        TarifficationReportDTO report = new TarifficationReportDTO(
                split[1],
                split[0],
                new ArrayList<>(callDataCount),
                Integer.parseInt(split[2]),
                cost
        );

        for (int i = 0; i < callDataCount; i++) {
            Optional<CallDataDTO> callData = callDataDeserializer.deserialize(reader);
            if (callData.isEmpty())
                throw new ParseException(0, "Tariffication report parse exception: invalid calls num");
            report.getCallsList().add(callData.get());
        }
        return Optional.of(report);
    }
}
