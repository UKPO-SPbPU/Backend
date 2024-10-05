package ru.trkpo.hrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.trkpo.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
public class HRSApp {
    public static void main(String[] args) {
        SpringApplication.run(HRSApp.class, args);
    }
}
