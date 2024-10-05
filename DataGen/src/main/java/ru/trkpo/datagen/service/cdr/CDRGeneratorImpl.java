package ru.trkpo.datagen.service.cdr;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.Pair;
import ru.trkpo.common.data.CDR;
import ru.trkpo.datagen.service.callType.CallTypeGenerator;
import ru.trkpo.datagen.service.dateTime.LocalDateTimeGenerator;
import ru.trkpo.datagen.service.phoneNumber.PhoneNumberGenerator;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CDRGeneratorImpl implements CDRGenerator {

    private final CallTypeGenerator callTypeGenerator;
    private final PhoneNumberGenerator phoneNumberGenerator;
    private final LocalDateTimeGenerator localDateTimeGenerator;

    @Override
    public CDR generateRecord() {
        Pair<LocalDateTime, LocalDateTime> dateTimePair = localDateTimeGenerator.generateDateTime();
        return new CDR(
                callTypeGenerator.generateCallType(),
                phoneNumberGenerator.generateNumber(),
                dateTimePair.getFirst(),
                dateTimePair.getSecond()
        );
    }
}
