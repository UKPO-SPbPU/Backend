package ru.trkpo.crm.data.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {
    private String fio;
    private String phoneNumber;
    private int numberPersonalAccount;
    private String contractDate;
    private String region;
    private String passport;
    private String birthDate;
    private String email;
}
