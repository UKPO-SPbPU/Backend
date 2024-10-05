package ru.trkpo.common.service.callHistory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.entity.CallHistory;

@Service
@AllArgsConstructor
public class CallHistoryServiceImpl implements CallHistoryService {

    private final CallHistoryRepository repository;

    @Override
    public void saveCall(CallHistory callHistory) {
        repository.save(callHistory);
    }
}
