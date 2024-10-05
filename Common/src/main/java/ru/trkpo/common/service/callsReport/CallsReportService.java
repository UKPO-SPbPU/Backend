package ru.trkpo.common.service.callsReport;

import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CallsReportService {
    Optional<TarifficationReportDTO> getCallsReport(String phoneNumber, LocalDateTime startDate, LocalDateTime endDate);
}
