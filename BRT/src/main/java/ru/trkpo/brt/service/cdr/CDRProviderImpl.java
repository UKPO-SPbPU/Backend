package ru.trkpo.brt.service.cdr;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.service.Deserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CDRProviderImpl implements CDRProvider {

    @Value("${brt-service.services.cdr.source-url}")
    private String filePath;

    private BufferedReader reader;

    private final Deserializer<CDR> cdrDeserializer;

    @Override
    public void init() throws IOException {
        UrlResource source = new UrlResource(filePath);
        reader = new BufferedReader(new InputStreamReader(source.getInputStream()));
    }

    @Override
    public List<CDR> getCDRsList() {
        List<CDR> cdrEntries = new ArrayList<>();
        try {
            while (true) {
                Optional<CDR> cdr = cdrDeserializer.deserialize(reader);
                if (cdr.isEmpty())
                    break;
                cdrEntries.add(cdr.get());
            }
            return cdrEntries;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
