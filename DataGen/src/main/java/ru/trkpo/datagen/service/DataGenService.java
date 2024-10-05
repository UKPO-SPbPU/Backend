package ru.trkpo.datagen.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.datagen.service.cdr.CDRGenerator;
import ru.trkpo.datagen.service.cdr.CDRWriter;
import ru.trkpo.datagen.service.dateTime.LocalDateTimeGenerator;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DataGenService {

    @Value("${data-gen-service.services.cdrs-generator.count}")
    private int cdrsCount;

    private final CDRGenerator cdrGenerator;
    private final LocalDateTimeGenerator localDateTimeGenerator;
    private final CDRWriter cdrWriter;

    public void generateCDRs() throws IOException {
        cdrWriter.init();
        for (int i = 0; i < cdrsCount; i++)
            cdrWriter.write(cdrGenerator.generateRecord());
        localDateTimeGenerator.updateDateTimeBoubds();
    }
}
