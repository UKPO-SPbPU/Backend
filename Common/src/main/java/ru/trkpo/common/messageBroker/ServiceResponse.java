package ru.trkpo.common.messageBroker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private ResponseStatus responseStatus;
    private String message;
}
