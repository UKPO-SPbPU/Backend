package ru.trkpo.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.trkpo.common.messageBroker.ServiceRequest;
import ru.trkpo.common.messageBroker.ServiceResponse;

@Service
@RequiredArgsConstructor
public class TarifficationMessanger {

    @Value("${crm-service.destination-queue.tariffication}")
    private String destination;

    private final JmsMessagingTemplate jmsMessagingTemplate;

    public ServiceResponse requestTariffication() {
        return jmsMessagingTemplate.convertSendAndReceive(destination, new ServiceRequest(destination),
                ServiceResponse.class);
    }
}
