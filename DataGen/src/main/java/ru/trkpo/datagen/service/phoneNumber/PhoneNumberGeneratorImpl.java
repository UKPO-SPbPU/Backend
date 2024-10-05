package ru.trkpo.datagen.service.phoneNumber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

import java.util.Random;

@Service
public class PhoneNumberGeneratorImpl implements PhoneNumberGenerator {

    @Value("${data-gen-service.services.phone-number-generator.existing-chance}")
    private double existingNumberChance;

    private final Random random = new Random();

    private final PhoneNumberService phoneNumberService;

    public PhoneNumberGeneratorImpl(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @Override
    public String generateNumber() {
        if (random.nextDouble() <= existingNumberChance)
            return phoneNumberService.findRandom().getPhoneNumber();
        else
            return generateNewNumber();
    }

    public String generateNewNumber() {
        return "7" + random.nextLong(1000000000L, 9999999999L);
    }
}
