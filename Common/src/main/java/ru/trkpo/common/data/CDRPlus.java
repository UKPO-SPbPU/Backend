package ru.trkpo.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CDRPlus {
    private String callTypeCode;
    private String phoneNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Duration duration;
    private String tariffCode;
}
