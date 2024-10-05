package ru.trkpo.brt.messageBroker;

import lombok.AllArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.trkpo.brt.service.TarifficationService;
import ru.trkpo.common.messageBroker.ResponseStatus;
import ru.trkpo.common.messageBroker.ServiceRequest;
import ru.trkpo.common.messageBroker.ServiceResponse;

@Service
@AllArgsConstructor
public class TarifficationListener {

    private final TarifficationService tarifficationService;

    @JmsListener(destination = "${brt-service.destination-queue.tariffication}")
    @SendTo("${brt-service.destination-queue.tariffication}")
    public ServiceResponse tarifficate(@Payload ServiceRequest request) {
        ServiceResponse response = tarifficationService.tarifficate();
        if (response.getResponseStatus().equals(ResponseStatus.SUCCESS))
            return new ServiceResponse(ResponseStatus.SUCCESS, "Successful client tariffication");
        else
            return new ServiceResponse(response.getResponseStatus(), response.getMessage());
    }
}
