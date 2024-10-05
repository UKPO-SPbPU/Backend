package ru.trkpo.common.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.trkpo.common.data.dto.CallDataDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class TarifficationReportDTO {
    private String phoneNumber;
    private String tariffCode;
    private List<CallDataDTO> callsList;
    private long totalMinutes;
    private BigDecimal totalCost;
}
