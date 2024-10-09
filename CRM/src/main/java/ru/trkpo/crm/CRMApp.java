package ru.trkpo.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import ru.trkpo.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
public class CRMApp {
    public static void main(String[] args) {
        SpringApplication.run(CRMApp.class, args);
    }

    @Bean
    public RestTemplate restTesmplate() {
        return new RestTemplate();
    }
}
