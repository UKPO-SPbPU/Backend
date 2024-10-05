package ru.trkpo.brt.service.cdrPlus;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.entity.Tariff;
import ru.trkpo.common.exception.NoDataFoundException;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

import java.time.Duration;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CDRPlusCreatorImpl implements CDRPlusCreator {

    private final PhoneNumberService phoneNumberService;

    @Override
    public Optional<CDRPlus> createRecord(CDR cdr) {
        Tariff tariff;
        try {
            tariff = phoneNumberService.findActiveTariff(cdr.getPhoneNumber());
        } catch (NoDataFoundException e) {
            return Optional.empty();
        }

        return Optional.of(new CDRPlus(
                cdr.getCallTypeCode(),
                cdr.getPhoneNumber(),
                cdr.getStartDateTime(),
                cdr.getEndDateTime(),
                Duration.between(cdr.getStartDateTime(), cdr.getEndDateTime()),
                tariff.getId()
        ));
    }
}
