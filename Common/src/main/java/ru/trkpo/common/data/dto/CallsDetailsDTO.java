package ru.trkpo.common.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.util.PGInterval;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallsDetailsDTO {
    private String tariffCode;
    private String phoneNumber;
    private PGInterval totalDuration;
    private BigDecimal totalCost;
    private long callsCount;
    private String callTypeCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal callCost;
}
