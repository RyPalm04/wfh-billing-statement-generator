package com.palmer.billingstatementgenerator.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Properties;

public class ApiConfig {
    private static final Logger log = LoggerFactory.getLogger(ApiConfig.class);

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.wfh-billing";
    private static final String CONFIG_PATH = CONFIG_DIR + "/api.properties";
    private static String baseUrl;
    private static HttpClient httpClient;

    private ApiConfig() {
    }

    public static synchronized void init() {
        if (baseUrl != null) {
            log.debug("Base URL is already initialized");
            return;
        }

        new File(CONFIG_DIR).mkdirs();

        Properties config = loadConfig();

        baseUrl = config.getProperty("api.base-url", "").trim();
        if (baseUrl.isEmpty()) {
            log.error("api.base-url not set in {}", CONFIG_PATH);
            throw new IllegalStateException("api.base-url not set in " + CONFIG_PATH);
        }

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        httpClient = HttpClient.newHttpClient();

        log.info("API configured - base URL: {}", baseUrl);
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getCatalogUrl() {
        return baseUrl + "/catalog";
    }

    public static String getStatementsUrl() {
        return baseUrl + "/statements";
    }

    public static HttpClient getHttpClient() {
        if (httpClient == null) {
            log.error("API http client not initialized");
            throw new IllegalStateException("ApiConfig.init() not called");
        }

        return httpClient;
    }

    private static Properties loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            writeConfigTemplate(configFile);
            log.error("API config file does not exist");
            throw new IllegalStateException("API configuration not found. A template has been created at " + CONFIG_PATH + ". Please set api.base-url and restart.");
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read API configuration from " + CONFIG_PATH, e);
        }
        return props;
    }

    private static void writeConfigTemplate(File configFile) {
        try (FileWriter w = new FileWriter(configFile)) {
            w.write("api.base-url=http://localhost:18080\n");
        } catch (IOException e) {
            log.warn("Could not write config template to {}", CONFIG_PATH, e);
        }
    }
}
