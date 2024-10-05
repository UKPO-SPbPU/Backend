package ru.trkpo.brt.service.cdrPlus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.service.Serializer;

import java.time.format.DateTimeFormatter;

@Service
public class CDRPlusSerializer implements Serializer<CDRPlus> {

    @Value("${brt-service.services.cdr.date-time-format}")
    private String dateTimeFormat;

    @Override
    public String serialize(CDRPlus item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return item.getCallTypeCode() + ", " +
                item.getPhoneNumber() + ", " +
                item.getStartDateTime().format(formatter) + ", " +
                item.getEndDateTime().format(formatter) + ", " +
                item.getDuration().toMinutes() + ", " +
                item.getTariffCode() + '\n';
    }
}
