package com.project.educationLab.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DBConnectionTest {

    private final Logger LOGGER = LoggerFactory.getLogger(DBConnectionTest.class);

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.username}")
    private String USERNAME;

    @Value("${spring.datasource.password}")
    private String PASSWORD;

    @Value("${spring.datasource.driver-class-name}")
    private String DRIVER;

    @Test
    public void propertiesTest() {
        LOGGER.info("url={}", URL);
        LOGGER.info("USERNAME={}", USERNAME);
        LOGGER.info("PASSWORD={}", PASSWORD);
        LOGGER.info("DRIVER={}", DRIVER);
        assertNotNull(URL);
        assertNotNull(USERNAME);
        assertNotNull(PASSWORD);
        assertNotNull(DRIVER);
    }

    @Test
    public void connectionTest() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        assertNotNull(connection);
    }
}
