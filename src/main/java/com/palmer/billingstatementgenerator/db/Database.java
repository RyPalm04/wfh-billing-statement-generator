package com.palmer.billingstatementgenerator.db;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String URL = "jdbc:h2:mem:wfh;DB_CLOSE_DELAY=-1";
    private static DataSource dataSource;

    private Database() {}

    public static synchronized void init() {
        if (dataSource != null) {
            log.debug("Database already initialized, skipping");
            return;
        }
        log.info("Initializing in-memory H2 database");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(URL);
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;
        runScript("/com/palmer/billingstatementgenerator/db/schema.sql");
        runScript("/com/palmer/billingstatementgenerator/db/seed.sql");
        log.info("Database initialized");
    }

    public static DataSource get() {
        if (dataSource == null) {
            throw new IllegalStateException("Database.init() has not been called");
        }
        return dataSource;
    }

    private static void runScript(String resourcePath) {
        log.debug("Running script: {}", resourcePath);
        try (InputStream in = Database.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + resourcePath);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                 Connection c = dataSource.getConnection()) {
                RunScript.execute(c, reader);
            }
        } catch (IOException | SQLException e) {
            log.error("Failed to run script: {}", resourcePath, e);
            throw new RuntimeException("Failed to run script: " + resourcePath, e);
        }
    }
}
