package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "client_details")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "number_personal_account", nullable = false, unique = true)
    private Integer numberPersonalAccount;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "passport", length = 10, unique = true)
    private String passport;

    @Column(name = "contract_date", nullable = false)
    private LocalDate contractDate;

    @Column(name = "contract_number", length = 15, nullable = false, unique = true)
    private String contractNumber;
}
