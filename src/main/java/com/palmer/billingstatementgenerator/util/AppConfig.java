package com.palmer.billingstatementgenerator.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility class for loading application configuration from an external properties file.
 * On startup, loads {@code config.properties} from the user's home directory and
 * registers all entries as system properties, making them available throughout the
 * application via {@link System#getProperty(String)}.
 *
 * <p>If the config file does not exist, default values are used for all settings.</p>
 *
 * <p>Example {@code ~/config.properties}:</p>
 * <pre>
 * wfh.sales.tax.rate=0.0825
 * </pre>
 */
public final class AppConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Path CONFIG_PATH = Paths.get("resources/com/palmer/billingstatementgenerator/properties/", CONFIG_FILE);

    static {
        load();
    }

    private AppConfig() {
    }

    /**
     * Loads the config file from the user's home directory and registers all
     * properties as system properties. Logs a warning to stderr if the file
     * exists but cannot be read.
     */
    private static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (InputStream in = new FileInputStream(CONFIG_PATH.toFile())) {
                Properties props = new Properties();
                props.load(in);
                props.forEach((key, value) ->
                        System.setProperty(key.toString(), value.toString())
                );
            } catch (IOException e) {
                System.err.println("Could not load config, using defaults: " + e.getMessage());
            }
        }
    }

    /**
     * Returns the configured sales tax rate as a {@link BigDecimal}.
     * Reads from the system property {@code wfh.sales.tax.rate}, defaulting to
     * {@code 0.00} if not set or if the value cannot be parsed.
     *
     * @return the sales tax rate (e.g. {@code 0.0825} for 8.25%)
     */
    public static BigDecimal getSalesTaxRate() {
        String val = System.getProperty("wfh.sales.tax.rate", "0.00");
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}