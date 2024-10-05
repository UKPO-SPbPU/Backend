package ru.trkpo.common.service.callsReport;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGInterval;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.CallsDetailsDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CallsReportServiceImpl implements CallsReportService{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<TarifficationReportDTO> getCallsReport(String phoneNumber, LocalDateTime startDate, LocalDateTime endDate) {
        List<CallsDetailsDTO> results = jdbcTemplate.query(
                "SELECT * FROM get_calls_report(?, ?, ?)",
                new Object[]{phoneNumber, startDate, endDate},
                new BeanPropertyRowMapper<>(CallsDetailsDTO.class));
        return Optional.ofNullable(parseResults(results));
    }

    private TarifficationReportDTO parseResults(List<CallsDetailsDTO> results) {
        if (results.isEmpty())
            return null;

        CallsDetailsDTO firstRow = results.get(0);
        String tariffCode = firstRow.getTariffCode();
        String phoneNumber = firstRow.getPhoneNumber();
        PGInterval interval = firstRow.getTotalDuration();
        long totalMinutes = interval.getHours() * 60L + interval.getMinutes();
        BigDecimal totalCost = firstRow.getTotalCost();
        List<CallDataDTO> callDataDTOList = new LinkedList<>();

        TarifficationReportDTO callsReport = new TarifficationReportDTO(
                phoneNumber,
                tariffCode,
                callDataDTOList,
                totalMinutes,
                totalCost
                );
        for (CallsDetailsDTO row : results) {
            String callTypeCode = row.getCallTypeCode();
            LocalDateTime startDateTime = row.getStartDate();
            LocalDateTime endDateTime = row.getEndDate();
            Duration duration = Duration.between(startDateTime, endDateTime);
            BigDecimal cost = row.getCallCost();

            CallDataDTO callDataDTO = new CallDataDTO(
                    callTypeCode,
                    startDateTime,
                    endDateTime,
                    duration,
                    cost
            );
            callDataDTOList.add(callDataDTO);
        }
        return callsReport;
    }
}
