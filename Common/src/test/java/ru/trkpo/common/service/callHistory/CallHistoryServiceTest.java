package ru.trkpo.common.service.callHistory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.entity.CallHistory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallHistoryServiceTest {

    @Mock
    private CallHistoryRepository repositoryMock;

    @InjectMocks
    private CallHistoryServiceImpl underTestService;

    @Test
    void testSaveCallShouldInvokeRepositorySaveMethod() {
        CallHistory callHistory = new CallHistory();
        when(repositoryMock.save(any(CallHistory.class))).thenReturn(callHistory);
        underTestService.saveCall(callHistory);
        verify(repositoryMock, times(1)).save(any(CallHistory.class));
    }
}
