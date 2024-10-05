package ru.trkpo.hrs.service.CallData;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.entity.CallHistory;
import ru.trkpo.common.data.entity.CallType;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.service.callHistory.CallHistoryService;
import ru.trkpo.common.service.callType.CallTypeService;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;

@Service
@AllArgsConstructor
public class CallDataSaver {

    private final CallHistoryService callHistoryService;
    private final PhoneNumberService phoneNumberService;
    private final CallTypeService callTypeService;

    public void saveCall(String phoneNumber, CallDataDTO callData) {
        PhoneNumber phone = phoneNumberService.findByPhoneNumber(phoneNumber);
        CallType callType = callTypeService.findByCallTypeCode(callData.getCallTypeCode());
        CallHistory callHistory = new CallHistory(
                null,
                phone,
                callType,
                callData.getStartDateTime(),
                callData.getEndDateTime(),
                callData.getCost());
        callHistoryService.saveCall(callHistory);
    }
}
