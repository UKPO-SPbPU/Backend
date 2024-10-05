package ru.trkpo.hrs.service.cdrPlus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CDRPlusProviderImpl implements CDRPlusProvider {

    @Value("${hrs-service.services.cdr-plus.source-url}")
    private String sourceURL;

    private BufferedReader reader;

    private final Deserializer<CDRPlus> cdrPlusDeserializer;

    public CDRPlusProviderImpl(Deserializer<CDRPlus> cdrPlusDeserializer) {
        this.cdrPlusDeserializer = cdrPlusDeserializer;
    }

    @Override
    public void init() throws IOException {
        UrlResource source = new UrlResource(sourceURL);
        reader = new BufferedReader(new InputStreamReader(source.getInputStream()));
    }

    @Override
    public List<CDRPlus> getCDRPlus() {
        List<CDRPlus> records = new ArrayList<>();
        try {
            while (true) {
                Optional<CDRPlus> record = cdrPlusDeserializer.deserialize(reader);
                if (record.isEmpty())
                    break;
                records.add(record.get());
            }
            return records;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
