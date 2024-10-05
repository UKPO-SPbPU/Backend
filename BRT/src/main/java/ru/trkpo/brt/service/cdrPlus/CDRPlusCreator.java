package ru.trkpo.brt.service.cdrPlus;

import ru.trkpo.common.data.CDR;
import ru.trkpo.common.data.CDRPlus;

import java.util.Optional;

public interface CDRPlusCreator {

    Optional<CDRPlus> createRecord(CDR cdrEntry);
}
