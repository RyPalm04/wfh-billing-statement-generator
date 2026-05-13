package com.palmer.billingstatementgenerator.db;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the application's single file-based H2 database instance.
 * The database file is stored in {@code ~/.wfh-billing/data.mv.db} and persists
 * across application launches. Schema and seed scripts are run only on first launch.
 *
 * <p>Call {@link #init()} once at startup before any DAO is used.
 * {@link #init()} is idempotent — subsequent calls are no-ops.
 * {@link #get()} throws {@link IllegalStateException} if called before {@link #init()}.</p>
 */
public final class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String DB_DIR = System.getProperty("user.home") + "/.wfh-billing";
    private static final String URL = "jdbc:h2:file:" + DB_DIR + "/data;DB_CLOSE_DELAY=-1";
    private static DataSource dataSource;

    private Database() {
    }

    /**
     * Initializes the file-based H2 database. On first launch, creates the database
     * directory and runs the schema and seed scripts. Subsequent launches skip seeding.
     * Safe to call multiple times; only the first call has any effect.
     */
    public static synchronized void init() {
        if (dataSource != null) {
            log.debug("Database already initialized, skipping");
            return;
        }
        File dbDir = new File(DB_DIR);
        boolean firstLaunch = !new File(DB_DIR + "/data.mv.db").exists();
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        log.info("Initializing H2 database at {}", DB_DIR);
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(URL);
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;
        if (firstLaunch) {
            log.info("First launch — running schema and seed scripts");
            runScript("/com/palmer/billingstatementgenerator/db/schema.sql");
            runScript("/com/palmer/billingstatementgenerator/db/seed.sql");
        }
        log.info("Database initialized");
    }

    /**
     * Returns the initialized {@link DataSource}.
     *
     * @return the application {@link DataSource}
     *
     * @throws IllegalStateException
     *         if {@link #init()} has not been called
     */
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
