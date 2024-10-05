package ru.trkpo.common.service.callType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.CallType;
import ru.trkpo.common.exception.NoDataFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallTypeServiceTest {

    @Mock
    private CallTypeRepository repositoryMock;

    @Mock
    private TransactionTemplate transactionTemplateMock;

    @InjectMocks
    private CallTypeServiceImpl underTestService;



    @Test
    void testFindByCallTypeCodeShouldReturnCallType() {
        CallType callType = new CallType("01", "Исходящий", null, null);
        String callCode = "01";
        Optional<CallType> callTypeOptional = Optional.of(callType);
        doAnswer(invocation -> {
            TransactionCallback<CallType> callback = (TransactionCallback<CallType>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(repositoryMock.findById(anyString())).thenReturn(callTypeOptional);

        CallType result = underTestService.findByCallTypeCode(callCode);

        assertThat(result).isEqualTo(callType);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(repositoryMock, times(1)).findById(anyString());
    }

    @Test
    void testFindByCallTypeCodeShouldThrowNoDataFoundException() {
        String callCode = "01";
        doAnswer(invocation -> {
            TransactionCallback<CallType> callback = (TransactionCallback<CallType>) invocation.getArguments()[0];
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(transactionTemplateMock).execute(any());
        when(repositoryMock.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.findByCallTypeCode(callCode))
                .isInstanceOf(NoDataFoundException.class);
        verify(transactionTemplateMock, times(1)).execute(any());
        verify(repositoryMock, times(1)).findById(anyString());
    }
}
