package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "internet_packages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InternetPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "package_of_mb", nullable = false)
    private Integer packageOfMb;

    @Column(name = "package_cost", nullable = false)
    private BigDecimal packageCost;

    @Column(name = "package_cost_per_mb", nullable = false)
    private Boolean packageCostPerMb;

    @Column(name = "extra_package_cost", nullable = false)
    private BigDecimal extraPackageCost;

    @Column(name = "extra_package_cost_per_mb", nullable = false)
    private Boolean extraPackageCostPerMb;

    @OneToMany(mappedBy = "internetPackage")
    private List<TariffConfig> tariffConfigList;
}
