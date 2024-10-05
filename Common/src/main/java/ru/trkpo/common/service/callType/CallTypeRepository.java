package ru.trkpo.common.service.callType;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.CallType;

@Repository
public interface CallTypeRepository extends CrudRepository<CallType, String> {
}
