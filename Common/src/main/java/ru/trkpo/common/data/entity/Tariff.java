package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tariff {

    @Id
    @Column(name = "id", length = 2)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "tariff")
    private List<PhoneNumber> phoneNumberList;

    @OneToMany(mappedBy = "tariff")
    private List<TariffConfig> tariffConfigList;
}
