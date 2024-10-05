package ru.trkpo.crm.data.tariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternetPackageDTO {
    private int packOfMB;
    private double packCost;
    private boolean packCostPerMB;
    private double extraPackCost;
    private boolean extraPackCostPerMB;
}
