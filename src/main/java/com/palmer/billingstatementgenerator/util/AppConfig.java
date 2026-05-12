package com.palmer.billingstatementgenerator.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), CONFIG_FILE);

    static {
        load();
    }

    private AppConfig() {}

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

    public static BigDecimal getSalesTaxRate() {
        String val = System.getProperty("wfh.sales.tax.rate", "0.00");
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}