package ru.trkpo.crm.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.trkpo.common.messageBroker.ResponseStatus;
import ru.trkpo.common.messageBroker.ServiceRequest;
import ru.trkpo.common.messageBroker.ServiceResponse;
import ru.trkpo.crm.TarifficationMessanger;
import ru.trkpo.crm.data.client.ClientInfo;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWebClient(registerRestTemplate = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CRMControllerIT {

    @MockBean
    private RestTemplate restTemplateMock;

    @MockBean
    private TarifficationMessanger messangerMock;

    @Autowired
    private MockMvc mockMvc;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withInitScript("db_init.sql");

    @Container
    static ArtemisContainer artemis = new ArtemisContainer("apache/activemq-artemis:latest-alpine")
            .withUser("admin")
            .withPassword("admin")
            .withExposedPorts(61616);

    private static Connection connection;

    @Value("${crm-service.destination-queue.tariffication}")
    private String destination;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @DynamicPropertySource
    static void artemisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.artemis.broker-url",
                () -> "tcp://%s:%d".formatted(
                        artemis.getHost(), artemis.getMappedPort(61616)));
        registry.add("spring.artemis.user", artemis::getUser);
        registry.add("spring.artemis.password", artemis::getPassword);
    }

    @BeforeAll
    static void beforeAll() throws SQLException {
        String url = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        connection = DriverManager.getConnection(url, username, password);
    }

    @Test
    void postgresConnectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void artemisConnectionEstablished() {
        assertThat(artemis.isCreated()).isTrue();
        assertThat(artemis.isRunning()).isTrue();
    }

    @Test
    void testTarifficateEndpoint() throws Exception {
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some response", HttpStatus.OK));
        when(messangerMock.requestTariffication())
        .thenAnswer(answer -> {
            jmsMessagingTemplate.convertAndSend(destination, new ServiceRequest(destination));
            jmsMessagingTemplate.receive(destination);
            return new ServiceResponse(ResponseStatus.SUCCESS, "Some message");
        });
        mockMvc.perform(patch("/api/tarifficate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isBoolean())
                .andDo(print());

        verify(restTemplateMock, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(messangerMock, times(1)).requestTariffication();
    }

    @Test
    public void testPayEndpoint() throws Exception {
        String phoneNumber = "79113332211";
        double moneyToAdd = 50.0;
        String query = "SELECT balance FROM phone_numbers WHERE phone_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, phoneNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    balance = balance.add(BigDecimal.valueOf(moneyToAdd));
                    mockMvc.perform(patch("/api/pay")
                                    .param("phoneNumber", phoneNumber)
                                    .param("moneyAdd", String.valueOf(moneyToAdd)))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$").isNumber())
                            .andExpect(content().string(balance.toString()))
                            .andDo(print());
                }
            }
        }
    }

    @Test
    void testChangeTariffEndpoint() throws Exception {
        String phoneNumber = "79113332211";
        String newTariffCode = "02";

        mockMvc.perform(patch("/api/changeTariff")
                        .param("phoneNumber", phoneNumber)
                        .param("tariffCode", newTariffCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(jsonPath("$").isNumber())
                .andExpect(content().string(newTariffCode))
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testChangeClientInfo() throws Exception {
        String phoneNumber = "79113332211";
        ClientInfo newClientInfo = ClientInfo.builder()
                .fio("LastName FirstName Patronymic")
                .phoneNumber("79113332211")
                .numberPersonalAccount(1)
                .contractDate("01122022")
                .region("Some Region")
                .birthDate("08082002")
                .email("temp@Gmail.com")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(patch("/api/user/info/{phoneNumber}", phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string("Client info successfully changed"))
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testGetReportEndpoint() throws Exception {
        String phoneNumber = "79113332211";
        LocalDateTime dateTimeStart = LocalDateTime.of(2023, 2, 1, 0, 0);
        LocalDateTime dateTimeEnd = LocalDateTime.of(2023, 2, 1, 0, 0);
        mockMvc.perform(get("/api/report")
                        .param("phoneNumber", phoneNumber)
                        .param("dateTimeStart", dateTimeStart.toString())
                        .param("dateTimeEnd", dateTimeEnd.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.phoneNumber", equalTo(phoneNumber)),
                        jsonPath("$.tariffCode").hasJsonPath(),
                        jsonPath("$.callsList").hasJsonPath(),
                        jsonPath("$.totalMinutes").hasJsonPath(),
                        jsonPath("$.totalCost").hasJsonPath()
                )
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testGetClientInfo() throws Exception {
        String phoneNumber = "79113332211";
        mockMvc.perform(get("/api/user/info/{phoneNumber}", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.fio").hasJsonPath(),
                        jsonPath("$.phoneNumber", equalTo(phoneNumber)),
                        jsonPath("$.numberPersonalAccount").hasJsonPath(),
                        jsonPath("$.contractDate").hasJsonPath(),
                        jsonPath("$.region").hasJsonPath(),
                        jsonPath("$.passport").hasJsonPath(),
                        jsonPath("$.birthDate").hasJsonPath(),
                        jsonPath("$.email").hasJsonPath()
                )
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testGetAllTariffsEndpoint() throws Exception {
        mockMvc.perform(get("/api/tariffs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tariffs").isArray())
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testGetClientsTariffEndpoint() throws Exception {
        String phoneNumber = "79113332211";
        mockMvc.perform(get("/api//tariff/{phoneNumber}", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tariff").isNotEmpty())
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }

    @Test
    void testTarifficateEndpointError() throws Exception {
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some response", HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(patch("/api/tarifficate"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string("Internal Server Error: 500 INTERNAL_SERVER_ERROR " +
                        "\"Ошибка генерации cdr-файла\""))
                .andDo(print());
        verify(restTemplateMock, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testPayEndpointError() throws Exception {
        String invalidPhoneNumber = "39113332211";
        double moneyToAdd = 50.0;

        mockMvc.perform(patch("/api/pay")
                        .param("phoneNumber", invalidPhoneNumber)
                        .param("moneyAdd", String.valueOf(moneyToAdd)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string("Internal Server Error: No such client"))
                .andDo(print()); // Проверка, что в ответе есть число (баланс)
    }
}
