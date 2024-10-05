package ru.trkpo.common.service.clientDetails;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.ClientDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@AllArgsConstructor
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Override
    public ClientDetails createNewClientDetails(Client client, String encodedPassword) {
        return ClientDetails.builder()
                .id(null)
                .client(client)
                .numberPersonalAccount(generatePersonalAccountNumber())
                .email(null)
                .password(encodedPassword)
                .region(generateRegion())
                .passport(null)
                .contractDate(LocalDate.now())
                .contractNumber(generateContractNumber())
                .build();
    }

    private String generateRegion() {
        String[] regions = {
                "Санкт-Петербург", "Ленинградская область", "Московская область",
                "Самарская область", "Владимирская область", "Республика Карелия"};
        Random random = new Random();
        return regions[random.nextInt(regions.length)];
    }

    private String generateContractNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter) + '0';
    }

    private int generatePersonalAccountNumber() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }
}
