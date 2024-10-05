package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "telephony_packages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TelephonyPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @ManyToOne
    @JoinColumn(name = "call_type_id")
    private CallType callType;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private TelecomOperator operator;

    @Column(name = "package_of_minutes", nullable = false)
    private Integer packageOfMinutes;

    @Column(name = "package_cost", nullable = false)
    private BigDecimal packageCost;

    @Column(name = "package_cost_per_minute", nullable = false)
    private Boolean packageCostPerMinute;

    @Column(name = "extra_package_cost", nullable = false)
    private BigDecimal extraPackageCost;

    @Column(name = "extra_package_cost_per_minute", nullable = false)
    private Boolean extraPackageCostPerMinute;

    @OneToMany(mappedBy = "telephonyPackage")
    private List<TariffConfig> tariffConfigList;
}
