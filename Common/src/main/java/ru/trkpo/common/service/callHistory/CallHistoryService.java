package ru.trkpo.common.service.callHistory;

import ru.trkpo.common.data.entity.CallHistory;

public interface CallHistoryService {
    void saveCall(CallHistory callHistory);
}
