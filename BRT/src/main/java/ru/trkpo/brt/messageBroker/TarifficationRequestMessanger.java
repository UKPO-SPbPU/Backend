package ru.trkpo.brt.messageBroker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.trkpo.common.messageBroker.ServiceRequest;
import ru.trkpo.common.messageBroker.ServiceResponse;

@Service
public class TarifficationRequestMessanger {

    @Value("${brt-service.destination-queue.tariffication-request}")
    private String destinationQueueName;

    private final JmsMessagingTemplate jmsMessagingTemplate;

    public TarifficationRequestMessanger(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    public ServiceResponse requestTariffication() {
        return jmsMessagingTemplate.convertSendAndReceive(
                destinationQueueName,
                new ServiceRequest(destinationQueueName),
                ServiceResponse.class
        );
    }
}
