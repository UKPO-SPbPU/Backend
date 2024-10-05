package ru.trkpo.brt.service.cdr;

import ru.trkpo.common.data.CDR;

import java.io.IOException;
import java.util.List;

public interface CDRProvider {

    void init() throws IOException;

    List<CDR> getCDRsList();
}
