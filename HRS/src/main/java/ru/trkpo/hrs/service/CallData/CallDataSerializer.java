package ru.trkpo.hrs.service.CallData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.service.Serializer;

import java.time.format.DateTimeFormatter;

@Service
public class CallDataSerializer implements Serializer<CallDataDTO> {

    @Value("${hrs-service.services.cdr-plus.date-time-format}")
    private String dateTimeFormat;

    @Override
    public String serialize(CallDataDTO item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return item.getCallTypeCode() + ", " +
                item.getStartDateTime().format(formatter) + ", " +
                item.getEndDateTime().format(formatter) + ", " +
                item.getDuration().toMinutes() + ", " +
                item.getCost() + '\n';
    }
}
