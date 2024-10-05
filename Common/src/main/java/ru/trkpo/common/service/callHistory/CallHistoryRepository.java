package ru.trkpo.common.service.callHistory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.CallHistory;

@Repository
public interface CallHistoryRepository extends CrudRepository<CallHistory, Integer> {
}
