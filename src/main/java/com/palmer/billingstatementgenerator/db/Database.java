package com.palmer.billingstatementgenerator.db;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:h2:mem:wfh;DB_CLOSE_DELAY=-1";
    private static DataSource dataSource;

    private Database() {}

    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(URL);
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;
        runScript("/db/schema.sql");
        runScript("/db/seed.sql");
    }

    public static DataSource get() {
        if (dataSource == null) {
            throw new IllegalStateException("Database.init() has not been called");
        }
        return dataSource;
    }

    private static void runScript(String resourcePath) {
        try (InputStream in = Database.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + resourcePath);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                 Connection c = dataSource.getConnection()) {
                RunScript.execute(c, reader);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to run script: " + resourcePath, e);
        }
    }
}
