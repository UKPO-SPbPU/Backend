package ru.trkpo.brt.service.tarifficationReport;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TarrificationReportProviderImpl implements TarifficationReportProvider {

    @Value("${brt-service.services.tariffication-report.source-url}")
    private String sourceURL;
    private BufferedReader reader;

    private final Deserializer<TarifficationReportDTO> reportDeserializer;

    @Override
    public void init() throws IOException {
        UrlResource source = new UrlResource(sourceURL);
        reader = new BufferedReader(new InputStreamReader(source.getInputStream()));
    }

    @Override
    public List<TarifficationReportDTO> getReports() {
        List<TarifficationReportDTO> reports = new ArrayList<>();
        try {
            while (true) {
                Optional<TarifficationReportDTO> report = reportDeserializer.deserialize(reader);
                if (report.isEmpty()) break;
                reports.add(report.get());
            }
            return reports;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
