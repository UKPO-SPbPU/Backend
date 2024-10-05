package ru.trkpo.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.trkpo.common.data.dto.TarifficationReportDTO;
import ru.trkpo.common.messageBroker.ResponseStatus;
import ru.trkpo.common.messageBroker.ServiceResponse;
import ru.trkpo.common.service.callsReport.CallsReportServiceImpl;
import ru.trkpo.common.service.phoneNumber.PhoneNumberServiceImpl;
import ru.trkpo.crm.TarifficationMessanger;
import ru.trkpo.crm.data.client.ClientInfo;
import ru.trkpo.crm.data.tariff.ClientTariffResponse;
import ru.trkpo.crm.data.tariff.TariffDTO;
import ru.trkpo.crm.data.tariff.TariffsResponse;
import ru.trkpo.crm.service.CRMService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CRMControllerTest {

    @Mock
    private TarifficationMessanger tarifficationMessangerMock;

    @Mock
    private PhoneNumberServiceImpl phoneNumberServiceMock;

    @Mock
    private CallsReportServiceImpl callsReportServiceMock;

    @Mock
    private CRMService crmServiceMock;

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private CRMController underTestController;
    private static final String DATAGEN_URL = "http://localhost:8081/data-gen/generate/cdr";

    @BeforeEach
    void setUp() {
        setField(underTestController, "dataGenURL", DATAGEN_URL);
    }

    @Test
    void testTarifficateShouldReturnResponseEntityWithStatusOK() {
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some response", HttpStatus.OK));
        when(tarifficationMessangerMock.requestTariffication())
                .thenReturn(new ServiceResponse(ResponseStatus.SUCCESS, "Some message"));

        ResponseEntity<Boolean> resultResponse = underTestController.tarifficate();

        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isTrue();
        verify(restTemplateMock, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(tarifficationMessangerMock, times(1)).requestTariffication();
    }

    @Test
    void testTarifficateShouldThrowExceptionBecauseOfCDRGenerationError() {
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some response", HttpStatus.INTERNAL_SERVER_ERROR));
        assertThatThrownBy(() -> underTestController.tarifficate()).isInstanceOf(ResponseStatusException.class);
        verify(restTemplateMock, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testTarifficateShouldThrowExceptionBecauseOfTarifficationMessangerError() {
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some response", HttpStatus.OK));
        when(tarifficationMessangerMock.requestTariffication())
                .thenReturn(new ServiceResponse(ResponseStatus.PRODUCER_ERROR, "Some message"));
        assertThatThrownBy(() -> underTestController.tarifficate()).isInstanceOf(ResponseStatusException.class);
        verify(restTemplateMock, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(tarifficationMessangerMock, times(1)).requestTariffication();
    }

    @Test
    void testPayShouldReturnResponseEntityWithStatusOK() {
        BigDecimal balance = BigDecimal.ONE;
        when(phoneNumberServiceMock.updateBalance(anyString(), any(BigDecimal.class))).thenReturn(balance);
        ResponseEntity<BigDecimal> resultResponse = underTestController.pay("71112223344", 1);
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(balance);
        verify(phoneNumberServiceMock, times(1)).updateBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    void testChangeTariffShouldReturnResponseEntityWithStatusOK() {
        String someString = "Some String";
        when(phoneNumberServiceMock.changeTariff(anyString(), anyString())).thenReturn(someString);
        ResponseEntity<String> resultResponse = underTestController.changeTariff("71112223344", "01");
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(someString);
        verify(phoneNumberServiceMock, times(1)).changeTariff(anyString(), anyString());
    }

    @Test
    void testChangeClientInfoShouldReturnResponseEntityWithStatusOK() {
        String someString = "Some String";
        when(crmServiceMock.changeClientInfo(anyString(), any(ClientInfo.class))).thenReturn(someString);
        ResponseEntity<String> resultResponse = underTestController.changeClientInfo("71112223344", new ClientInfo());
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(someString);
        verify(crmServiceMock, times(1)).changeClientInfo(anyString(), any(ClientInfo.class));
    }

    @Test
    void testGetReportShouldReturnResponseEntityWithStatusOKAndEmptyReport() {
        TarifficationReportDTO tarifficationReportDTO = new TarifficationReportDTO(
                "71112223344",
                "01",
                null,
                0,
                BigDecimal.ZERO);
        LocalDateTime ldt = LocalDateTime.now();
        when(callsReportServiceMock.getCallsReport(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(tarifficationReportDTO));

        ResponseEntity<TarifficationReportDTO> resultResponse = underTestController
                .getReport("71112223344", ldt, ldt);

        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(tarifficationReportDTO);
        verify(callsReportServiceMock, times(1))
                .getCallsReport(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetReportShouldReturnResponseEntityWithStatusOKAndReport() {
        TarifficationReportDTO tarifficationReportDTO = new TarifficationReportDTO(
                "71112223344",
                "01",
                null,
                0,
                BigDecimal.ZERO);
        LocalDateTime ldt = LocalDateTime.now();
        TariffDTO tariffDTO = TariffDTO.builder()
                .id("01").title("Title").description("Description")
                .internetPackage(null).telephonyPackage(null)
                .build();
        when(callsReportServiceMock.getCallsReport(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(crmServiceMock.getClientTariff(anyString())).thenReturn(new ClientTariffResponse(tariffDTO));

        ResponseEntity<TarifficationReportDTO> resultResponse = underTestController
                .getReport("71112223344", ldt, ldt);

        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(tarifficationReportDTO);
        verify(callsReportServiceMock, times(1))
                .getCallsReport(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(crmServiceMock, times(1)).getClientTariff(anyString());
    }

    @Test
    void testGetClientInfoShouldReturnResponseEntityWithStatusOK() {
        ClientInfo clientInfo = ClientInfo.builder()
                .fio("LastName FirstName Patronymic")
                .phoneNumber("71112223344")
                .numberPersonalAccount(1)
                .contractDate("Some Date")
                .region("Some Region")
                .birthDate("Some Birthday")
                .build();
        when(crmServiceMock.getClientInfo(anyString())).thenReturn(clientInfo);
        ResponseEntity<ClientInfo> resultResponse = underTestController.getClientInfo("71112223344");
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(clientInfo);
        verify(crmServiceMock, times(1)).getClientInfo(anyString());
    }

    @Test
    void testGetAllTariffsShouldReturnResponseEntityWithStatusOK() {
        TariffDTO tariffDTO = TariffDTO.builder()
                .id("01").title("Title").description("Description")
                .internetPackage(null).telephonyPackage(null)
                .build();
        when(crmServiceMock.getAllTariffs()).thenReturn(new TariffsResponse(List.of(tariffDTO)));
        ResponseEntity<TariffsResponse> resultResponse = underTestController.getAllTariffs();
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(new TariffsResponse(List.of(tariffDTO)));
        verify(crmServiceMock, times(1)).getAllTariffs();
    }

    @Test
    void testGetClientTariffShouldReturnResponseEntityWithStatusOK() {
        TariffDTO tariffDTO = TariffDTO.builder()
                .id("01").title("Title").description("Description")
                .internetPackage(null).telephonyPackage(null)
                .build();
        when(crmServiceMock.getClientTariff(anyString())).thenReturn(new ClientTariffResponse(tariffDTO));
        ResponseEntity<ClientTariffResponse> resultResponse = underTestController
                .getClientTariff("71112223344");
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(new ClientTariffResponse(tariffDTO));
        verify(crmServiceMock, times(1)).getClientTariff(anyString());
    }
}
