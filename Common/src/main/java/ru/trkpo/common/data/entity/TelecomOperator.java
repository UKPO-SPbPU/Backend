package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "telecom_operators")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TelecomOperator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "operator")
    private List<TelephonyPackage> telephonyPackageList;
}
