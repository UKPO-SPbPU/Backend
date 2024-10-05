package ru.trkpo.brt.service.cdrPlus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.service.Serializer;

import java.io.*;

@Service
public class CDRPlusWriterImpl implements CDRPlusWriter {

    @Value("${brt-service.services.cdr-plus.file-path}")
    private String cdrPlusFilePath;

    private final Serializer<CDRPlus> serializer;

    public CDRPlusWriterImpl(Serializer<CDRPlus> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(CDRPlus record) throws IOException {
        try (FileWriter writer = new FileWriter(cdrPlusFilePath, true)) {
            writer.write(serializer.serialize(record));
        }
    }

    @Override
    public void init() throws IOException {
        try (FileWriter writer = new FileWriter(cdrPlusFilePath, false)) {
            writer.write("");
        }
    }
}
