package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "phone_numbers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "phone_number", length = 16, nullable = false)
    private String phoneNumber;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @OneToMany(mappedBy = "phoneNumber")
    private List<CallHistory> callHistoryList;
}
