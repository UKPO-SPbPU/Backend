package ru.trkpo.datagen.service.cdr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.service.Serializer;

import java.time.format.DateTimeFormatter;

@Service
public class CDRSerializer implements Serializer<CDR> {

    @Value("${data-gen-service.services.date-time-generator.format}")
    private String dateTimeFormat;

    @Override
    public String serialize(CDR item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return item.getCallTypeCode() +
                ", " +
                item.getPhoneNumber() +
                ", " +
                item.getStartDateTime().format(formatter) +
                ", " +
                item.getEndDateTime().format(formatter) +
                '\n';
    }
}
