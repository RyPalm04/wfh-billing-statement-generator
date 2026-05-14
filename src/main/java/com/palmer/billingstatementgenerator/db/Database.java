package com.palmer.billingstatementgenerator.db;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the application's PostgreSQL database connection and schema migrations.
 * Connection details are read from {@code ~/.wfh-billing/database.properties} on first launch.
 * Schema migrations are applied automatically via Flyway on every startup.
 *
 * <p>Call {@link #init()} once at startup before any DAO is used.
 * {@link #init()} is idempotent — subsequent calls are no-ops.
 * {@link #get()} throws {@link IllegalStateException} if called before {@link #init()}.</p>
 */
public final class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String DB_DIR = System.getProperty("user.home") + "/.wfh-billing";
    private static final String CONFIG_PATH = DB_DIR + "/database.properties";
    private static DataSource dataSource;

    private Database() {
    }

    /**
     * Initializes the database using connection details from {@code ~/.wfh-billing/database.properties}.
     * Applies any pending Flyway migrations before returning. Safe to call multiple times;
     * only the first call has any effect.
     *
     * @throws IllegalStateException
     *         if the configuration file does not exist (a template is created automatically)
     * @throws RuntimeException
     *         if the database connection fails or migrations cannot be applied
     */
    public static synchronized void init() {
        if (dataSource != null) {
            log.debug("Database already initialized, skipping");
            return;
        }
        new File(DB_DIR).mkdirs();
        Properties config = loadConfig();

        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try (Connection probe = DriverManager.getConnection(url, user, password)) {
            log.debug("Database connection verified");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database — check " + CONFIG_PATH, e);
        }

        dataSource = new DriverManagerDataSource(url, user, password);
        migrate(dataSource);
        log.info("Database initialized");
    }

    /**
     * Initializes the database using the provided {@link DataSource}. Intended for use
     * in tests, where an in-memory datasource is supplied directly.
     *
     * @param ds
     *         the {@link DataSource} to use
     */
    static synchronized void initWithDataSource(DataSource ds) {
        if (dataSource != null) {
            log.debug("Database already initialized, skipping");
            return;
        }
        dataSource = ds;
        migrate(ds);
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

    private static void migrate(DataSource ds) {
        Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    private static Properties loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            writeConfigTemplate(configFile);
            throw new IllegalStateException(
                    "Database configuration not found. A template has been created at " + CONFIG_PATH +
                    ". Please fill in your PostgreSQL connection details and restart.");
        }
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read database configuration from " + CONFIG_PATH, e);
        }
        return props;
    }

    private static void writeConfigTemplate(File configFile) {
        try (FileWriter w = new FileWriter(configFile)) {
            w.write("db.url=jdbc:postgresql://host:5432/dbname\n");
            w.write("db.user=username\n");
            w.write("db.password=password\n");
        } catch (IOException e) {
            log.warn("Could not write config template to {}", CONFIG_PATH, e);
        }
    }

    private static class DriverManagerDataSource implements DataSource {
        private final String url;
        private final String user;
        private final String password;

        DriverManagerDataSource(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }

        @Override
        public Connection getConnection(String user, String password) throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }

        @Override
        public java.io.PrintWriter getLogWriter() {
            return null;
        }

        @Override
        public void setLogWriter(java.io.PrintWriter pw) {
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public void setLoginTimeout(int seconds) {
        }

        @Override
        public java.util.logging.Logger getParentLogger() {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("Not a wrapper");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return false;
        }
    }
}