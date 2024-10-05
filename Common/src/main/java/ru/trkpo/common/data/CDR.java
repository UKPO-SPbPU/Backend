package ru.trkpo.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CDR {
    private String callTypeCode;
    private String phoneNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
