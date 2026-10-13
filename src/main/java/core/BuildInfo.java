package core;

import core.util.HOLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {

    private static final HOLogger log = HOLogger.instance();

    private static final String RESOURCE_NAME = "/build-info.properties";
    private static final Properties PROPERTIES = loadProperties();

    private BuildInfo() {
    }

    public static String versionStr() {
        return PROPERTIES.getProperty("version", "unknown version");
    }

    public static String devStageStr() {
        return PROPERTIES.getProperty("DEV_STAGE", "unknown stage");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = BuildInfo.class.getResourceAsStream(RESOURCE_NAME)) {
            if (inputStream == null) {
                log.warning(BuildInfo.class, "Properties file not found: %s".formatted(RESOURCE_NAME));
                return properties;
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            log.error(BuildInfo.class, "Cannot read properties file: %s".formatted(RESOURCE_NAME));
            return properties;
        }
    }
}
