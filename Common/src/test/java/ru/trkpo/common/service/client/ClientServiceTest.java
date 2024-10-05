package ru.trkpo.common.service.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.exception.NoDataFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepositoryMock;

    @Mock
    private TransactionTemplate transactionTemplateMock;

    @InjectMocks
    private ClientServiceImpl underTestService;

    @Test
    void testCreateNewClientShouldReturnNewClient() {
        String[] correctFIOOne = new String[] {"Lastname", "Firstname", "Patronymic"};
        String[] correctFIOTwo = new String[] {"Lastname", "Firstname"};
        for (String[] item : List.of(correctFIOOne, correctFIOTwo)) {
            Client expectedClient = Client.builder()
                    .firstName(item[1]).lastName(item[0]).patronymic(item.length == 3 ? item[2] : null)
                    .build();
            Client resultClient = underTestService.createNewClient(item);
            assertThat(resultClient.getFirstName()).isEqualTo(expectedClient.getFirstName());
            assertThat(resultClient.getLastName()).isEqualTo(expectedClient.getLastName());
            assertThat(resultClient.getPatronymic()).isEqualTo(expectedClient.getPatronymic());
            assertThat(resultClient.getAge()).isNull();
            assertThat(resultClient.getBirthday()).isNull();
            assertThat(resultClient.getPhoneNumber()).isNull();
            assertThat(resultClient.getClientDetails()).isNull();
        }
    }

    @Test
    void testCreateNewClientShouldThrowNoDataFoundException() {
        String[] invalidFIOOne = new String[] {"Lastname"};
        String[] invalidFIOTwo = new String[] {"Lastname", "Firstname", "Patronymic", "Some other string"};
        for (String[] item : List.of(invalidFIOOne, invalidFIOTwo)) {
            assertThatThrownBy(() -> underTestService.createNewClient(item))
                    .isInstanceOf(NoDataFoundException.class);
        }
    }

    @Test
    void testSaveClientShouldReturnSavedClient() {
        Client someClient = new Client();
        when(clientRepositoryMock.save(any(Client.class))).thenReturn(someClient);
        Client resultClient = underTestService.saveClient(someClient);
        assertThat(someClient).isEqualTo(resultClient);
        verify(clientRepositoryMock, times(1)).save(any(Client.class));
    }

    @Test
    void testFindByPhoneNumberShouldReturnClient() {
        String phoneNumber = "71112223344";
        Optional<Client> clientOptional = Optional.of(new Client());
        doAnswer(invocation -> {
            TransactionCallback<Client> callback = (TransactionCallback<Client>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(clientRepositoryMock.findByPhoneNumber(anyString())).thenReturn(clientOptional);

        Client resultClient = underTestService.findByPhoneNumber(phoneNumber);

        assertThat(resultClient).isEqualTo(clientOptional.get());
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(clientRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testFindByPhoneNumberShouldThrowNoDataFoundException() {
        String phoneNumber = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<Client> callback = (TransactionCallback<Client>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(clientRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.findByPhoneNumber(phoneNumber))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(clientRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testIsPhoneNumberExistsShouldReturnTrue() {
        String phoneNumber = "71112223344";
        Optional<Client> clientOptional = Optional.of(new Client());
        doAnswer(invocation -> {
            TransactionCallback<Client> callback = (TransactionCallback<Client>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(clientRepositoryMock.findByPhoneNumber(anyString())).thenReturn(clientOptional);

        boolean result = underTestService.isPhoneNumberExists(phoneNumber);

        assertThat(result).isTrue();
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(clientRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testIsPhoneNumberExistsShouldReturnFalse() {
        String phoneNumber = "71112223344";
        doAnswer(invocation -> {
            TransactionCallback<Client> callback = (TransactionCallback<Client>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(clientRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        boolean result = underTestService.isPhoneNumberExists(phoneNumber);

        assertThat(result).isFalse();
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(clientRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }
}
