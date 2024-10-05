package ru.trkpo.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trkpo.common.data.entity.*;
import ru.trkpo.common.service.client.ClientServiceImpl;
import ru.trkpo.common.service.phoneNumber.PhoneNumberServiceImpl;
import ru.trkpo.common.service.tariff.TariffServiceImpl;
import ru.trkpo.crm.data.client.ClientInfo;
import ru.trkpo.crm.data.tariff.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CRMServiceTest {

    @Mock
    private ClientServiceImpl clientServiceMock;

    @Mock
    private TariffServiceImpl tariffServiceMock;

    @Mock
    private PhoneNumberServiceImpl phoneNumberServiceMock;

    @InjectMocks
    private CRMService underTestService;
    private static final String DATE_FORMAT = "ddMMyyyy";
    private static final String INCOMING_CALL_CODE = "02";
    private Client client;
    private PhoneNumber phoneNumber;
    private ClientDetails clientDetails;

    @BeforeEach
    void setUp() {
        setField(underTestService, "dateFormat", DATE_FORMAT);
        setField(underTestService, "incomingCallCode", INCOMING_CALL_CODE);
        createClientInfo();
    }

    @Test
    void testGetClientInfoShouldReturnCorrectClientInfo() {
        ClientInfo expectedInfo = buildClientInfo();
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);

        ClientInfo resultInfo = underTestService.getClientInfo(phoneNumber.getPhoneNumber());

        assertThat(resultInfo).isEqualTo(expectedInfo);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
    }
    @Test
    void testGetClientInfoShouldReturnCorrectClientInfoWithoutPatronymicAndBirthday() {
        ClientInfo expectedInfo = buildClientInfo();
        String[] fioArray = expectedInfo.getFio().split(" ");
        String fio = fioArray[0] + " " + fioArray[1];
        expectedInfo.setFio(fio);
        expectedInfo.setBirthDate(null);
        client.setPatronymic(null);
        client.setBirthday(null);
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);

        ClientInfo resultInfo = underTestService.getClientInfo(phoneNumber.getPhoneNumber());

        assertThat(resultInfo).isEqualTo(expectedInfo);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testChangeClientInfoShouldReturnString() {
        ClientInfo clientInfo = buildClientInfo();
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);
        when(clientServiceMock.saveClient(any(Client.class))).thenReturn(null);

        String resultString = underTestService.changeClientInfo(phoneNumber.getPhoneNumber(), clientInfo);

        assertThat(resultString).isNotNull().isNotEmpty();
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testChangeClientInfoShouldThrowExceptionBecauseOfFIO() {
        ClientInfo clientInfoOne = buildClientInfo();
        ClientInfo clientInfoTwo = buildClientInfo();
        String invalidFIOOne = "Lastname";
        String invalidFIOTwo = "Lastname Firstname Patronymic SomeOtherString";
        clientInfoOne.setFio(invalidFIOOne);
        clientInfoTwo.setFio(invalidFIOTwo);
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);

        assertThatThrownBy(() -> underTestService.changeClientInfo(phoneNumber.getPhoneNumber(), clientInfoOne))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> underTestService.changeClientInfo(phoneNumber.getPhoneNumber(), clientInfoTwo))
                .isInstanceOf(RuntimeException.class);
        verify(clientServiceMock, times(2)).findByPhoneNumber(anyString());
    }

    @Test
    void testChangeClientInfoShouldThrowExceptionBecauseChangeablePhoneNumberAlreadyExists() {
        ClientInfo clientInfo = buildClientInfo();
        String newPhoneNumber = "79998887766";
        clientInfo.setPhoneNumber(newPhoneNumber);
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);
        when(clientServiceMock.isPhoneNumberExists(anyString())).thenReturn(true);

        assertThatThrownBy(() -> underTestService.changeClientInfo(phoneNumber.getPhoneNumber(), clientInfo))
                .isInstanceOf(RuntimeException.class);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testGetAllTariffsShouldReturnTariffResponse() {
        Tariff tariffOne = new Tariff("01", "Title", "Description", null, null);
        Tariff tariffTwo = new Tariff("02", "Another title", "Another description", List.of(phoneNumber), null);
        CallType callType = new CallType("01", "Исходящий", null, null);
        TelephonyPackage telephonyPackage = new TelephonyPackage(1, callType, null, 10, BigDecimal.ONE, true, BigDecimal.ONE, true, null);
        InternetPackage internetPackage = new InternetPackage(1, 10, BigDecimal.ONE, true, BigDecimal.ONE, true, null);
        TariffConfig tariffConfigOne = new TariffConfig(1L, tariffOne, telephonyPackage, internetPackage);
        TariffConfig tariffConfigTwo = new TariffConfig(1L, tariffTwo, null, null);
        tariffOne.setTariffConfigList(List.of(tariffConfigOne));
        tariffTwo.setTariffConfigList(List.of(tariffConfigTwo));
        List<Tariff> tariffList = new ArrayList<>();
        tariffList.add(tariffOne);
        tariffList.add(tariffTwo);
        tariffList.add(null);
        TelephonyPackageDTO telephonyPackageDTO = TelephonyPackageDTO.builder()
                .incomingCall(telephonyPackage.getCallType().getId().equals("02"))
                .packOfMinutes(telephonyPackage.getPackageOfMinutes())
                .packCost(telephonyPackage.getPackageCost().doubleValue())
                .packCostPerMinute(telephonyPackage.getPackageCostPerMinute())
                .extraPackCost(telephonyPackage.getExtraPackageCost().doubleValue())
                .extraPackCostPerMinute(telephonyPackage.getExtraPackageCostPerMinute())
                .build();
        InternetPackageDTO internetPackageDTO = InternetPackageDTO.builder()
                .packOfMB(internetPackage.getPackageOfMb())
                .packCost(internetPackage.getPackageCost().doubleValue())
                .packCostPerMB(internetPackage.getPackageCostPerMb())
                .extraPackCost(internetPackage.getExtraPackageCost().doubleValue())
                .extraPackCostPerMB(internetPackage.getExtraPackageCostPerMb())
                .build();
        TariffDTO tariffDTOOne = TariffDTO.builder()
                .id(tariffOne.getId()).title(tariffOne.getTitle()).description(tariffOne.getDescription())
                .telephonyPackage(telephonyPackageDTO).internetPackage(internetPackageDTO)
                .build();
        TariffDTO tariffDTOTwo = TariffDTO.builder()
                .id(tariffTwo.getId()).title(tariffTwo.getTitle()).description(tariffTwo.getDescription())
                .build();
        TariffDTO tariffDTOThree = new TariffDTO();
        TariffsResponse expectedResponse = new TariffsResponse(List.of(tariffDTOOne, tariffDTOTwo, tariffDTOThree));
        when(tariffServiceMock.getAllTariffs()).thenReturn(tariffList);

        TariffsResponse resultResponse = underTestService.getAllTariffs();

        assertThat(resultResponse).isEqualTo(expectedResponse);
        verify(tariffServiceMock, times(1)).getAllTariffs();
    }

    @Test
    void testGetClientTariffShouldReturnClientTariffResponse() {
        Tariff tariff = new Tariff("01", "Title", "Description", null, null);
        TariffDTO tariffDTO = TariffDTO.builder()
                .id(tariff.getId()).title(tariff.getTitle()).description(tariff.getDescription())
                .build();
        ClientTariffResponse expectedResponse = new ClientTariffResponse(tariffDTO);
        when(phoneNumberServiceMock.findActiveTariff(anyString())).thenReturn(tariff);

        ClientTariffResponse resultResponse = underTestService.getClientTariff(phoneNumber.getPhoneNumber());

        assertThat(resultResponse).isEqualTo(expectedResponse);
        verify(phoneNumberServiceMock, times(1)).findActiveTariff(anyString());

    }

    private void createClientInfo() {
        LocalDate currentDate = LocalDate.now();
        String firstName = "Vasya";
        String lastName = "Pupkin";
        String patronymic = "Sergeevich";
        LocalDate birthday = LocalDate.of(2002, 8, 8);
        String phoneNumberString = "71112223344";
        String email = "temp@email.com";
        String passport = "1111222333";
        String someString = "Some string";
        int someNumber = 1234567890;
        client = Client.builder()
                .id(1L).firstName(firstName).lastName(lastName).patronymic(patronymic).birthday(birthday)
                .build();
        phoneNumber = PhoneNumber.builder()
                .clientId(1L).client(client).phoneNumber(phoneNumberString).balance(BigDecimal.valueOf(500))
                .build();
        clientDetails = ClientDetails.builder()
                .id(1L).client(client).numberPersonalAccount(someNumber).email(email)
                .password(someString).region(someString).passport(passport)
                .contractDate(currentDate).contractNumber(someString)
                .build();
        client.setPhoneNumber(phoneNumber);
        client.setClientDetails(clientDetails);
    }

    private ClientInfo buildClientInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return ClientInfo.builder()
                .fio(client.getLastName() + " " + client.getFirstName() + " " + client.getPatronymic())
                .phoneNumber(phoneNumber.getPhoneNumber())
                .numberPersonalAccount(clientDetails.getNumberPersonalAccount())
                .contractDate(clientDetails.getContractDate().format(formatter))
                .region(clientDetails.getRegion()).passport(clientDetails.getPassport())
                .birthDate(client.getBirthday().format(formatter)).email(clientDetails.getEmail())
                .build();
    }
}