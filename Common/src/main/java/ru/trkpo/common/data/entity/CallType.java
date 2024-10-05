package ru.trkpo.common.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "call_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallType {

    @Id
    private String id;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @OneToMany(mappedBy = "callType")
    private List<CallHistory> callHistoryList;

    @OneToMany(mappedBy = "callType")
    private List<TelephonyPackage> telephonyPackageList;
}
