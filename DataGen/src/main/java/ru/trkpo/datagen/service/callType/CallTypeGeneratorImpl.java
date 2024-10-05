package ru.trkpo.datagen.service.callType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CallTypeGeneratorImpl implements CallTypeGenerator {

    @Value("${data-gen-service.services.call-type-generator.incoming-call-chance}")
    private double incomingCallChance;

    @Value("${data-gen-service.services.call-type-generator.incoming-call-code}")
    private String incomingCallCode;

    @Value("${data-gen-service.services.call-type-generator.outcoming-call-code}")
    private String outcomingCallCode;

    private final Random random = new Random();

    @Override
    public String generateCallType() {
        return random.nextDouble() <= incomingCallChance ? incomingCallCode : outcomingCallCode;
    }
}
