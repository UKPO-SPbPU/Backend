package ru.trkpo.common.service.clientDetails;

import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.ClientDetails;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ClientDetailsServiceTest {

    private static final ClientDetailsServiceImpl underTestService = new ClientDetailsServiceImpl();

    @Test
    void testCreateNewClientDetailsShouldReturnNewClientDetails() {
        Client client = new Client();
        String encodedPassword = "Some encoded password";
        ClientDetails resultClientDetails = underTestService.createNewClientDetails(client, encodedPassword);
        assertThat(resultClientDetails.getClient()).isEqualTo(client);
        assertThat(resultClientDetails.getNumberPersonalAccount()).isBetween(0, Integer.MAX_VALUE);
        assertThat(resultClientDetails.getEmail()).isNull();
        assertThat(resultClientDetails.getPassword()).isEqualTo(encodedPassword);
        assertThat(resultClientDetails.getRegion()).isNotNull().isNotEmpty();
        assertThat(resultClientDetails.getPassport()).isNull();
        assertThat(resultClientDetails.getContractDate()).isNotNull().isBeforeOrEqualTo(LocalDate.now());
        assertThat(resultClientDetails.getContractNumber()).isNotNull().hasSameSizeAs("yyyyMMddHHmmss0");
    }
}
