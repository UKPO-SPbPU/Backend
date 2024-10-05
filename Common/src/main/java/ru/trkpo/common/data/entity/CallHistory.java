package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_phone_number_id")
    private PhoneNumber phoneNumber;

    @ManyToOne
    @JoinColumn(name = "call_type_id")
    private CallType callType;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "cost")
    private BigDecimal cost;
}
