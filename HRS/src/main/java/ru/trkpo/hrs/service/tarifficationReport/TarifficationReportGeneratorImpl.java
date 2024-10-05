package ru.trkpo.hrs.service.tarifficationReport;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.dto.CallDataDTO;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.service.tariff.TariffService;
import ru.trkpo.hrs.service.CallData.CallDataSaver;
import ru.trkpo.hrs.service.cdrPlus.CDRPlusProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class TarifficationReportGeneratorImpl implements TarifficationReportGenerator {

    private final CDRPlusProvider cdrPlusProvider;
    private final TarifficationReportWriter reportWriter;
    private final TariffService tariffService;
    private final CallDataSaver callDataSaver;

    @Override
    public void generateReports() throws IOException {
        cdrPlusProvider.init();
        reportWriter.init();

        List<CDRPlus> cdrPlusList = cdrPlusProvider.getCDRPlus();
        HashMap<String, TarifficationReportDTO> reportsHashMap = new HashMap<>();
        cdrPlusList.forEach(cdrPlus -> createReport(reportsHashMap, cdrPlus));
        for (String phoneNumber : reportsHashMap.keySet()) {
            reportWriter.write(reportsHashMap.get(phoneNumber));
        }
    }

    private void createReport(HashMap<String, TarifficationReportDTO> reports, CDRPlus cdrPlus) {
        String phoneNumber = cdrPlus.getPhoneNumber();
        TarifficationReportDTO report;
        if (!reports.containsKey(phoneNumber)) {
            report = new TarifficationReportDTO(
                    phoneNumber,
                    cdrPlus.getTariffCode(),
                    new ArrayList<>(),
                    0,
                    BigDecimal.ZERO);
            reports.put(phoneNumber, report);
        }
        report = reports.get(phoneNumber);
        CallDataDTO callData = new CallDataDTO(
                cdrPlus.getCallTypeCode(),
                cdrPlus.getStartDateTime(),
                cdrPlus.getEndDateTime(),
                cdrPlus.getDuration(),
                tariffService.applyTariff(cdrPlus));
        report.getCallsList().add(callData);
        report.setTotalMinutes(report.getTotalMinutes() + callData.getDuration().toMinutes());
        report.setTotalCost(report.getTotalCost().add(callData.getCost()));
        callDataSaver.saveCall(phoneNumber, callData);
    }
}
