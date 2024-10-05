package ru.trkpo.crm.data.tariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffDTO {
    private String id;
    private String title;
    private String description;
    private TelephonyPackageDTO telephonyPackage;
    private InternetPackageDTO internetPackage;
}
