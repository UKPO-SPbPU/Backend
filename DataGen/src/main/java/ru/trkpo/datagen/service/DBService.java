package ru.trkpo.datagen.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class DBService {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    private final ResourceDatabasePopulator databasePopulator =
            new ResourceDatabasePopulator(false, false, "UTF-8",
                    new ClassPathResource("data.sql"));
    private final ResourceDatabasePopulator databaseCreator =
            new ResourceDatabasePopulator(true, false, "UTF-8",
                    new ClassPathResource("scheme.sql"));
    private final ResourceDatabasePopulator databaseDropper =
            new ResourceDatabasePopulator(false, false, "UTF-8",
                    new ClassPathResource("drop.sql"));

    public DBService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void populate() {
        databaseCreator.execute(dataSource);
        databasePopulator.execute(dataSource);
    }

    public void truncate() {
        databaseDropper.execute(dataSource);
        databaseCreator.execute(dataSource);
        createStoredProcedures();
    }

    public void reset() {
        truncate();
        populate();
    }

    private void createStoredProcedures() {
        ClassPathResource resource = new ClassPathResource("procedures.sql");
        String sqlQuery;
        try {
            sqlQuery = FileCopyUtils.copyToString(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jdbcTemplate.execute(sqlQuery);
    }
}
