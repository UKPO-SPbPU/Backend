package ru.trkpo.brt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.trkpo.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
public class BRTApp {
    public static void main(String[] args) {
        SpringApplication.run(BRTApp.class, args);
    }
}
