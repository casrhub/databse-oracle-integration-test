package com.example.demo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import java.sql.ResultSet;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class InitializedDatabaseTest {
    /**
     * Use a containerized Oracle Database instance for testing.
     */
    static OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
            .withStartupTimeout(Duration.ofMinutes(5))
            .withUsername("testuser")
            .withPassword("testpwd");

    static OracleDataSource ds;

    @BeforeAll
    static void setUp() throws SQLException {
        oracleContainer.start();
        // Configure the OracleDataSource to use the database container
        ds = new OracleDataSource();
        ds.setURL(oracleContainer.getJdbcUrl());
        ds.setUser(oracleContainer.getUsername());
        ds.setPassword(oracleContainer.getPassword());
    }

    /**
     * Verifies the containerized database connection.
     * @throws SQLException
     */
    @Test
    void getUsuario() throws SQLException {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS WHERE USUARIO_ID = 102")) {
            Assertions.assertTrue(rs.next());
            assertThat(rs.getString("NOMBRE")).isEqualTo("Cesar Alan Silva Ramos");
        }
    }
}