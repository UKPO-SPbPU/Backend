package ru.trkpo.hrs.service.tarifficationReport;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Serializer;

import java.io.FileWriter;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TarifficationReportWriterImpl implements TarifficationReportWriter {

    @Value("${hrs-service.services.tariffication-report.file-path}")
    private String reportFilePath;

    private final Serializer<TarifficationReportDTO> reportSerializer;

    @Override
    public void init() throws IOException {
        try (FileWriter writer = new FileWriter(reportFilePath, false)) {
            writer.write("");
        }
    }

    @Override
    public void write(TarifficationReportDTO report) throws IOException {
        try (FileWriter writer = new FileWriter(reportFilePath, true)) {
            writer.write(reportSerializer.serialize(report));
        }
    }
}
