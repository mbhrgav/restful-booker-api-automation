package com.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "config.properties file was not found"
                );
            }

            PROPERTIES.load(inputStream);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load config.properties",
                    exception
            );
        }
    }

    private ConfigManager() {
        // Prevent object creation
    }

    public static String getProperty(String key) {

        String systemProperty = System.getProperty(key);

        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty.trim();
        }

        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing configuration property: " + key
            );
        }

        return value.trim();
    }
}