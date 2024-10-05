package ru.trkpo.common.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CallDataDTO {
    private String callTypeCode;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Duration duration;
    private BigDecimal cost;
}
