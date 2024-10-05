package ru.trkpo.common.service.callType;

import ru.trkpo.common.data.entity.CallType;

public interface CallTypeService {
    CallType findByCallTypeCode(String code);
}
