package ru.trkpo.datagen.service.cdr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDR;
import ru.trkpo.common.service.Serializer;

import java.io.*;

@Service
public class CDRWriterImpl implements CDRWriter {

    @Value("${data-gen-service.services.cdrs-generator.cdr-file-path}")
    private String cdrFilePath;

    private final Serializer<CDR> serializer;

    public CDRWriterImpl(Serializer<CDR> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(CDR record) throws IOException {
        try (FileWriter writer = new FileWriter(cdrFilePath, true)) {
            writer.write(serializer.serialize(record));
        }
    }

    @Override
    public void init() throws IOException {
        try (FileWriter writer = new FileWriter(cdrFilePath, false)) {
            writer.write("");
        }
    }
}
