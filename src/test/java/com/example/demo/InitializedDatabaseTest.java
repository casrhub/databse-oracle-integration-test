package com.example.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import oracle.jdbc.pool.OracleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.oracle.OracleContainer;
import static org.assertj.core.api.Assertions.assertThat;

public class InitializedDatabaseTest {

    /**
     * Use a containerized Oracle Database instance for testing.
     */
    static OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
            .withStartupTimeout(Duration.ofMinutes(5))
            .withUsername("testuser")
            .withPassword("testpwd")
            .withInitScript("schema.sql")
            .withCreateContainerCmdModifier(cmd -> cmd.withPlatform("linux/amd64")); 

    static OracleDataSource ds;

    @BeforeAll
    static void setUp() throws SQLException {
        // Disable Ryuk container to avoid ARM64 startup issues
        System.setProperty("testcontainers.ryuk.disabled", "true");

        // Start the Oracle container
        oracleContainer.start();

        // Configure the OracleDataSource to use the database container
        ds = new OracleDataSource();
        ds.setURL(oracleContainer.getJdbcUrl());
        ds.setUser(oracleContainer.getUsername());
        ds.setPassword(oracleContainer.getPassword());
    }

    /**
     * Verifies the database is initialized with the expected user
     */
    @Test
    void getUsuario() throws SQLException {
        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS WHERE USUARIO_ID = 102")) {

            Assertions.assertTrue(rs.next(), "No se encontró el usuario con ID = 102");
            assertThat(rs.getString("NOMBRE")).isEqualTo("Cesar Alan Silva Ramos");
        }
    }
}
