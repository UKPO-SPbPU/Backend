package ru.trkpo.common.service.phoneNumber;

import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.data.entity.Tariff;

import java.math.BigDecimal;

public interface PhoneNumberService {

    PhoneNumber findByPhoneNumber(String phoneNumber);
    PhoneNumber findRandom();
    Tariff findActiveTariff(String phoneNumber);
    BigDecimal updateBalance(String phoneNumber, BigDecimal money);
    String changeTariff(String phoneNumber, String tariffCode);
    PhoneNumber createNewPhoneNumber(Client client, String phoneNumber);
    PhoneNumber save(PhoneNumber phoneNumber);
}
