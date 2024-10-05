package ru.trkpo.brt.service.cdrPlus;

import ru.trkpo.common.data.CDRPlus;

import java.io.IOException;

public interface CDRPlusWriter {

    void write(CDRPlus record) throws IOException;

    void init() throws IOException;
}
