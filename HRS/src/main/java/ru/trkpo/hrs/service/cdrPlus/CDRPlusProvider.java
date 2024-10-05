package ru.trkpo.hrs.service.cdrPlus;

import ru.trkpo.common.data.CDRPlus;

import java.io.IOException;
import java.util.List;

public interface CDRPlusProvider {
    void init() throws IOException;
    List<CDRPlus> getCDRPlus();
}
