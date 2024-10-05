package ru.trkpo.hrs.service.tarifficationReport;

import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.io.IOException;

public interface TarifficationReportWriter {
    void init() throws IOException;
    void write(TarifficationReportDTO report) throws IOException;
}
