package com.example.demo;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.spring.api.DBRider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DBRider
public abstract class AbstractIntegrationTest {

    @Autowired
    private DataSource dataSource;

    public ConnectionHolder connectionHolder = () -> dataSource.getConnection();
}

