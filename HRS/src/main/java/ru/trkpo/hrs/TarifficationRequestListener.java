package ru.trkpo.hrs;

import lombok.AllArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.trkpo.common.messageBroker.ResponseStatus;
import ru.trkpo.common.messageBroker.ServiceRequest;
import ru.trkpo.common.messageBroker.ServiceResponse;
import ru.trkpo.hrs.service.tarifficationReport.TarifficationReportGenerator;

@Service
@AllArgsConstructor
public class TarifficationRequestListener {

    private final TarifficationReportGenerator reportGenerator;

    @JmsListener(destination = "${hrs-service.destination-queue.tariffication-request}")
    @SendTo("${hrs-service.destination-queue.tariffication-request}")
    public ServiceResponse generateReports(@Payload ServiceRequest request) {
        try {
            reportGenerator.generateReports();
            return new ServiceResponse(ResponseStatus.SUCCESS, "Successfully generated tariffication reports");
        } catch (Exception e) {
            return new ServiceResponse(ResponseStatus.CONSUMER_ERROR, "HRS service error: " + e.getMessage());
        }
    }
}
