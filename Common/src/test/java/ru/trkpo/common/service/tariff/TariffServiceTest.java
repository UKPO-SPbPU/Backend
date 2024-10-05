package ru.trkpo.common.service.tariff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.CDRPlus;
import ru.trkpo.common.data.entity.Tariff;
import ru.trkpo.common.data.entity.TariffConfig;
import ru.trkpo.common.data.entity.TelephonyPackage;
import ru.trkpo.common.exception.NoDataFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepositoryMock;

    @Mock
    private TransactionTemplate transactionTemplateMock;

    @InjectMocks
    private TariffServiceImpl underTestService;

    @Test
    void testFindByCodeShouldReturnTariff() {
        String tariffCode = "01";
        Tariff tariff = new Tariff(tariffCode, "Title", "Description", null, null);
        doAnswer(invocation -> {
            TransactionCallback<Tariff> callback = (TransactionCallback<Tariff>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(tariffRepositoryMock.findByIdEquals(anyString())).thenReturn(Optional.of(tariff));

        Tariff resultTariff = underTestService.findByCode(tariffCode);

        assertThat(resultTariff.getId()).isEqualTo(tariffCode);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(tariffRepositoryMock, times(1)).findByIdEquals(anyString());
    }

    @Test
    void testFindByCodeShouldThrowNoDataFoundException() {
        String tariffCode = "01";
        doAnswer(invocation -> {
            TransactionCallback<Tariff> callback = (TransactionCallback<Tariff>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(tariffRepositoryMock.findByIdEquals(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.findByCode(tariffCode))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(tariffRepositoryMock, times(1)).findByIdEquals(anyString());
    }

    @Test
    void testApplyTariffShouldReturnTotalCost() {
        CDRPlus cdrPlus = buildCDRPlus();
        TelephonyPackage telephonyPackage = new TelephonyPackage(
                1, null, null, 10,
                BigDecimal.valueOf(100), false, BigDecimal.valueOf(5), true, null
        );
        TariffConfig tariffConfig = new TariffConfig(1L, null, telephonyPackage, null);
        Tariff tariff = new Tariff(
                "01", "Title", "Description", null, List.of(tariffConfig)
        );
        tariffConfig.setTariff(tariff);
        long totalCallDurationInMinutes = cdrPlus.getDuration().toMinutes();
        long expectedValue = 100 + (totalCallDurationInMinutes - 10) * 5;
        when(tariffRepositoryMock.findByIdEquals(anyString())).thenReturn(Optional.of(tariff));

        BigDecimal resultCost = underTestService.applyTariff(cdrPlus);

        assertThat(resultCost).isEqualTo(BigDecimal.valueOf(expectedValue));
        verify(tariffRepositoryMock, times(1)).findByIdEquals(anyString());
    }

    @Test
    void testApplyTariffShouldThrowNoDataFoundException() {
        CDRPlus cdrPlus = buildCDRPlus();
        when(tariffRepositoryMock.findByIdEquals(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTestService.applyTariff(cdrPlus))
                .isInstanceOf(NoDataFoundException.class);
        verify(tariffRepositoryMock, times(1)).findByIdEquals(anyString());
    }

    @Test
    void testGetAllTariffsShouldReturnTariffsList() {
        List<Tariff> tariffList = List.of(new Tariff(), new Tariff(), new Tariff());
        when(tariffRepositoryMock.findAll()).thenReturn(tariffList);
        List<Tariff> resultList = underTestService.getAllTariffs();
        assertThat(resultList).isEqualTo(tariffList);
        verify(tariffRepositoryMock, times(1)).findAll();
    }

    private CDRPlus buildCDRPlus() {
        String callTypeCode = "01";
        String phoneNumberString = "71112223344";
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(20);
        Duration duration = Duration.between(startDateTime, endDateTime);
        String tariffCode = "03";
        return new CDRPlus(
                callTypeCode, phoneNumberString, startDateTime ,endDateTime, duration, tariffCode
        );
    }
}
