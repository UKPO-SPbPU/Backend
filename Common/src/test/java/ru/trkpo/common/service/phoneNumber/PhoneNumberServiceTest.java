package ru.trkpo.common.service.phoneNumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.data.entity.Tariff;
import ru.trkpo.common.exception.DataAlreadyExistsException;
import ru.trkpo.common.exception.NoDataFoundException;
import ru.trkpo.common.service.tariff.TariffRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneNumberServiceTest {

    @Mock
    private PhoneNumberRepository phoneNumberRepositoryMock;

    @Mock
    private TariffRepository tariffRepositoryMock;

    @Mock
    private TransactionTemplate transactionTemplateMock;

    @InjectMocks
    private PhoneNumberServiceImpl underTestService;

    @Test
    void testFindByPhoneNumberShouldReturnPhoneNumber() {
        String phoneNumber = "71112223344";
        Optional<PhoneNumber> phoneNumberOptional = Optional.of(new PhoneNumber());
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(phoneNumberOptional);

        PhoneNumber resultPhoneNumber = underTestService.findByPhoneNumber(phoneNumber);

        assertThat(resultPhoneNumber).isEqualTo(phoneNumberOptional.get());
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testFindByPhoneNumberShouldThrowNoDataFoundException() {
        String phoneNumber = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.findByPhoneNumber(phoneNumber))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testFindRandomShouldReturnPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber();
        Page<PhoneNumber> phoneNumberPage = new PageImpl<>(List.of(phoneNumber));
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.count()).thenReturn(1L);
        when(phoneNumberRepositoryMock.findAll(any(Pageable.class))).thenReturn(phoneNumberPage);

        PhoneNumber resultPhoneNumber = underTestService.findRandom();

        assertThat(resultPhoneNumber).isEqualTo(phoneNumber);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).count();
        verify(phoneNumberRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindRandomShouldReturnNull() {
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.count()).thenReturn(1L);
        when(phoneNumberRepositoryMock.findAll(any(Pageable.class))).thenReturn(Page.empty());

        PhoneNumber resultPhoneNumber = underTestService.findRandom();

        assertThat(resultPhoneNumber).isNull();
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).count();
        verify(phoneNumberRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindActiveTariffShouldReturnTariff() {
        String phoneNumberString = "71112223344";
        Tariff tariff = new Tariff();
        PhoneNumber phoneNumber = new PhoneNumber(
                1L, null, phoneNumberString, BigDecimal.ONE, tariff, null
        );
        doAnswer(invocation -> {
            TransactionCallback<Tariff> callback = (TransactionCallback<Tariff>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.of(phoneNumber));

        Tariff resultTariff = underTestService.findActiveTariff(phoneNumberString);

        assertThat(resultTariff).isEqualTo(tariff);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testFindActiveTariffShouldThrowNoDataFoundException() {
        String phoneNumberString = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<Tariff> callback = (TransactionCallback<Tariff>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.findActiveTariff(phoneNumberString))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testUpdateBalanceShouldReturnUpdatedBalance() {
        String phoneNumberString = "71112223344";
        BigDecimal initBalance = BigDecimal.ZERO;
        BigDecimal moneyToAdd = BigDecimal.ONE;
        PhoneNumber phoneNumber = new PhoneNumber(
                1L, null, phoneNumberString, initBalance, null, null
        );
        doAnswer(invocation -> {
            TransactionCallback<BigDecimal> callback = (TransactionCallback<BigDecimal>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.of(phoneNumber));
        doNothing().when(phoneNumberRepositoryMock).updatePhoneNumberBalance(any(BigDecimal.class), anyLong());

        BigDecimal updatedBalanceResult = underTestService.updateBalance(phoneNumberString, moneyToAdd);
        assertThat(updatedBalanceResult).isEqualTo(initBalance.add(moneyToAdd));
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
        verify(phoneNumberRepositoryMock, times(1))
                .updatePhoneNumberBalance(any(BigDecimal.class), anyLong());
    }

    @Test
    void testUpdateBalanceShouldThrowNoDataFoundException() {
        String phoneNumberString = "71112223344";
        BigDecimal moneyToAdd = BigDecimal.ONE;
        doAnswer(invocation -> {
            TransactionCallback<BigDecimal> callback = (TransactionCallback<BigDecimal>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.updateBalance(phoneNumberString, moneyToAdd))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testChangeTariffShouldReturnTariffID() {
        String phoneNumberString = "71112223344";
        String tariffCode = "01";
        Tariff tariff = new Tariff(tariffCode, "Title", "Description", null, null);
        PhoneNumber phoneNumber = new PhoneNumber(
                1L, null, phoneNumberString, BigDecimal.ONE, null, null
        );
        doAnswer(invocation -> {
            TransactionCallback<String> callback = (TransactionCallback<String>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.of(phoneNumber));
        when(tariffRepositoryMock.findByIdEquals(anyString())).thenReturn(Optional.of(tariff));
        doNothing().when(phoneNumberRepositoryMock).updatePhoneNumberTariff(any(Tariff.class), anyLong());

        String result = underTestService.changeTariff(phoneNumberString, tariffCode);

        assertThat(result).isEqualTo(tariffCode);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
        verify(tariffRepositoryMock, times(1)).findByIdEquals(anyString());
        verify(phoneNumberRepositoryMock, times(1))
                .updatePhoneNumberTariff(any(Tariff.class), anyLong());
    }

    @Test
    void testChangeTariffShouldThrowNoDataFoundException() {
        String phoneNumberString = "71112223344";
        String tariffCode = "01";
        doAnswer(invocation -> {
            TransactionCallback<String> callback = (TransactionCallback<String>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.changeTariff(phoneNumberString, tariffCode))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testCreateNewPhoneNumberShouldReturnPhoneNumber() {
        Client client = new Client();
        String phoneNumberString = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        PhoneNumber resultPhoneNumber = underTestService.createNewPhoneNumber(client, phoneNumberString);

        assertThat(resultPhoneNumber.getClientId()).isNull();
        assertThat(resultPhoneNumber.getClient()).isEqualTo(client);
        assertThat(resultPhoneNumber.getPhoneNumber()).isEqualTo(phoneNumberString);
        assertThat(resultPhoneNumber.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(resultPhoneNumber.getTariff()).isNull();
        assertThat(resultPhoneNumber.getCallHistoryList()).isNull();
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testCreateNewPhoneNumberShouldThrowDataAlreadyExistsException() {
        Client client = new Client();
        String phoneNumberString = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.of(new PhoneNumber()));

        assertThatThrownBy(() -> underTestService.createNewPhoneNumber(client, phoneNumberString))
                .isInstanceOf(DataAlreadyExistsException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testSaveShouldReturnPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber();
        doAnswer(invocation -> {
            TransactionCallback<PhoneNumber> callback = (TransactionCallback<PhoneNumber>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(phoneNumberRepositoryMock.save(any(PhoneNumber.class))).thenReturn(phoneNumber);

        PhoneNumber resultPhoneNumber = underTestService.save(phoneNumber);

        assertThat(resultPhoneNumber).isEqualTo(phoneNumber);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(phoneNumberRepositoryMock, times(1)).save(any(PhoneNumber.class));
    }
}
