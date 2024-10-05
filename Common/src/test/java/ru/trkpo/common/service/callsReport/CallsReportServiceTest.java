package ru.trkpo.common.service.callsReport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PGInterval;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.trkpo.common.data.dto.CallsDetailsDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallsReportServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @InjectMocks
    private CallsReportServiceImpl underTestService;

    @Test
    void testGetCallsReportShouldReturnNotEmptyOptionalOfTarifficationReportDTO() {
        String phoneNumber = "71112223344";
        int callsCount = 10;
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);
        List<CallsDetailsDTO> callsDetailsDTOList = generateCallsDetailsDTOList(phoneNumber, callsCount);
        CallsDetailsDTO firstItem = callsDetailsDTOList.get(0);
        PGInterval duration = firstItem.getTotalDuration();
        int totalDurationInMinutes = duration.getHours() * 60 + duration.getMinutes();
        when(jdbcTemplateMock.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(callsDetailsDTOList);

        Optional<TarifficationReportDTO> resultOptional = underTestService.
                getCallsReport(phoneNumber, startDateTime, endDateTime);
        TarifficationReportDTO result = resultOptional.get();
        assertThat(result.getTariffCode()).isEqualTo(firstItem.getTariffCode());
        assertThat(result.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(result.getCallsList().size()).isEqualTo(callsDetailsDTOList.size());
        assertThat(result.getTotalMinutes()).isEqualTo(totalDurationInMinutes);
        assertThat(result.getTotalCost()).isEqualTo(firstItem.getTotalCost());
        verify(jdbcTemplateMock, times(1))
                .query(anyString(), any(Object[].class), any(RowMapper.class));
    }

    @Test
    void testGetCallsReportShouldReturnEmptyOptionalOfTarifficationReportDTO() {
        String phoneNumber = "71112223344";
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);
        when(jdbcTemplateMock.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of());

        Optional<TarifficationReportDTO> resultOptional = underTestService.
                getCallsReport(phoneNumber, startDateTime, endDateTime);
        assertThat(resultOptional).isEmpty();
        verify(jdbcTemplateMock, times(1))
                .query(anyString(), any(Object[].class), any(RowMapper.class));
    }

    private List<CallsDetailsDTO> generateCallsDetailsDTOList(String phoneNumber, int callsCount) {
        Random random = new Random();
        String tariffCode = "0" + random.nextInt(1,10);
        int durationMinutes = random.nextInt(callsCount, callsCount * 60);
        PGInterval totalDuration = new PGInterval(
                0, 0, 0, durationMinutes / 60, durationMinutes % 60, 0
        );
        BigDecimal totalCost = BigDecimal.valueOf(random.nextDouble(100, 1000));

        List<CallsDetailsDTO> callsDetailsDTOList = new ArrayList<>();
        for (int i = 0; i < callsCount; i++) {
            String callTypeCode = random.nextInt(2) == 0 ? "01" : "02";
            long minusMinutes = durationMinutes - ((long) durationMinutes / callsCount * i);
            int plusMinutes = durationMinutes / callsCount;
            LocalDateTime startDate = LocalDateTime.now().minusMinutes(minusMinutes);
            LocalDateTime endDate = startDate.plusMinutes(plusMinutes);
            BigDecimal callCost = totalCost.divide(BigDecimal.valueOf(callsCount), RoundingMode.CEILING);
            CallsDetailsDTO callsDetailsDTO = new CallsDetailsDTO(
                    tariffCode, phoneNumber, totalDuration, totalCost,
                    callsCount, callTypeCode, startDate, endDate, callCost
            );
            callsDetailsDTOList.add(callsDetailsDTO);
        }
        return callsDetailsDTOList;
    }
}
