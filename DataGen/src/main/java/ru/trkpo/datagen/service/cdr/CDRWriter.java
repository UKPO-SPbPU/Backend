package ru.trkpo.datagen.service.cdr;

import ru.trkpo.common.data.CDR;

import java.io.IOException;

public interface CDRWriter {
    void write(CDR record) throws IOException;

    void init() throws IOException;
}
