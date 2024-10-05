package ru.trkpo.datagen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.trkpo.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
public class DataGenApp {
    public static void main(String[] args) {
        SpringApplication.run(DataGenApp.class, args);
    }
}
