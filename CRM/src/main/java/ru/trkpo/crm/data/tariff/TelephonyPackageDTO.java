package ru.trkpo.crm.data.tariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelephonyPackageDTO {
    private Boolean incomingCall;
    private int packOfMinutes;
    private double packCost;
    private boolean packCostPerMinute;
    private double extraPackCost;
    private boolean extraPackCostPerMinute;
}
