package ru.trkpo.brt.service.tarifficationReport;

import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.io.IOException;
import java.util.List;

public interface TarifficationReportProvider {

    void init() throws IOException;

    List<TarifficationReportDTO> getReports();
}
