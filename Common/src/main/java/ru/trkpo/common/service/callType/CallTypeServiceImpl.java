package ru.trkpo.common.service.callType;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.CallType;
import ru.trkpo.common.exception.NoDataFoundException;

@Service
@AllArgsConstructor
public class CallTypeServiceImpl implements CallTypeService {

    private final CallTypeRepository repository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public CallType findByCallTypeCode(String code) {
        return transactionTemplate.execute(status -> repository.findById(code)
                .orElseThrow(() -> new NoDataFoundException("There is no such call type")));
    }
}
