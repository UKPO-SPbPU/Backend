package ru.trkpo.hrs.service.tarifficationReport;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.Serializer;

@Service
@AllArgsConstructor
public class TarifficationReportSerializer implements Serializer<TarifficationReportDTO> {

    private final Serializer<CallDataDTO> callDataSerializer;

    @Override
    public String serialize(TarifficationReportDTO item) {
        String string = item.getTariffCode() + ", " +
                item.getPhoneNumber() + ", " +
                item.getTotalMinutes() + ", " +
                item.getTotalCost() + ", " +
                item.getCallsList().size() + '\n';

        StringBuilder sb = new StringBuilder(string);
        for (CallDataDTO callDataDTO : item.getCallsList()) {
            sb.append(callDataSerializer.serialize(callDataDTO));
        }
        return sb.toString();
    }
}
