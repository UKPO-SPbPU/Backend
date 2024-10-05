package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tariffs_config")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(name = "telephony_package_id")
    private TelephonyPackage telephonyPackage;

    @ManyToOne
    @JoinColumn(name = "internet_package_id")
    private InternetPackage internetPackage;
}
