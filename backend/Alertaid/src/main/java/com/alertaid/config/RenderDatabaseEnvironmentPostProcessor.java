package com.alertaid.config;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Normalizes Render-provided environment variables (DATABASE_URL, PORT, etc.)
 * into the keys that Spring Boot expects so deployments require minimal setup.
 */
public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RenderDatabaseEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "renderAutoConfiguration";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> overrides = new LinkedHashMap<>();
        contributeServerPort(environment, overrides);
        contributeDatabaseSettings(environment, overrides);

        if (!overrides.isEmpty()) {
            log.info("Applying Render specific overrides: {}", overrides.keySet());
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, overrides));
        }
    }

    private void contributeServerPort(ConfigurableEnvironment environment, Map<String, Object> overrides) {
        String port = environment.getProperty("PORT");
        if (StringUtils.hasText(port)) {
            overrides.put("server.port", port);
        }
    }

    private void contributeDatabaseSettings(ConfigurableEnvironment environment, Map<String, Object> overrides) {
        if (StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_URL"))) {
            log.info("SPRING_DATASOURCE_URL is already set, skipping DATABASE_URL parsing");
            return;
        }

        String rawUrl = firstNonEmpty(environment.getProperty("DATABASE_JDBC_URL"),
            environment.getProperty("DATABASE_URL"),
            environment.getProperty("JAWSDB_URL"),
            environment.getProperty("CLEARDB_DATABASE_URL"));

        if (!StringUtils.hasText(rawUrl)) {
            log.warn("No DATABASE_URL found in environment variables. Database connection may fail.");
            return;
        }

        log.info("Found DATABASE_URL, parsing connection string (length: {})", rawUrl.length());
        // Log first 50 chars for debugging (without exposing full credentials)
        String urlPreview = rawUrl.length() > 50 ? rawUrl.substring(0, 50) + "..." : rawUrl;
        log.info("DATABASE_URL preview: {}", urlPreview.replaceAll(":[^:@]+@", ":****@"));

        RenderDatabaseDetails details = RenderDatabaseDetails.from(rawUrl.trim());
        if (details == null) {
            log.error("Unable to parse database URL. Raw URL format may be unsupported.");
            return;
        }

        log.info("Successfully parsed database connection: host={}, port={}, database={}", 
            extractHost(details.jdbcUrl()), extractPort(details.jdbcUrl()), extractDatabase(details.jdbcUrl()));

        overrides.put("spring.datasource.url", details.jdbcUrl());

        if (!StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_USERNAME")) && StringUtils.hasText(details.username())) {
            overrides.put("spring.datasource.username", details.username());
            log.info("Set database username from connection string");
        }
        if (!StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_PASSWORD")) && StringUtils.hasText(details.password())) {
            overrides.put("spring.datasource.password", details.password());
            log.info("Set database password from connection string");
        }
        if (!StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_DRIVER")) && StringUtils.hasText(details.driverClassName())) {
            overrides.put("spring.datasource.driver-class-name", details.driverClassName());
            log.info("Set database driver: {}", details.driverClassName());
        }
        // Always set dialect if we successfully parsed the database URL to prevent SQL dialect errors
        if (StringUtils.hasText(details.hibernateDialect())) {
            overrides.put("spring.jpa.properties.hibernate.dialect", details.hibernateDialect());
            log.info("Setting Hibernate dialect to: {}", details.hibernateDialect());
        }
    }

    private String extractHost(String jdbcUrl) {
        try {
            int start = jdbcUrl.indexOf("://") + 3;
            int end = jdbcUrl.indexOf(":", start);
            if (end < 0) end = jdbcUrl.indexOf("/", start);
            return end > 0 ? jdbcUrl.substring(start, end) : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String extractPort(String jdbcUrl) {
        try {
            int start = jdbcUrl.indexOf("://") + 3;
            int colon = jdbcUrl.indexOf(":", start);
            if (colon < 0) return "default";
            int end = jdbcUrl.indexOf("/", colon);
            return end > 0 ? jdbcUrl.substring(colon + 1, end) : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String extractDatabase(String jdbcUrl) {
        try {
            int lastSlash = jdbcUrl.lastIndexOf("/");
            if (lastSlash < 0) return "unknown";
            int question = jdbcUrl.indexOf("?", lastSlash);
            return question > 0 ? jdbcUrl.substring(lastSlash + 1, question) : jdbcUrl.substring(lastSlash + 1);
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String firstNonEmpty(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private record RenderDatabaseDetails(
        String jdbcUrl,
        String username,
        String password,
        String driverClassName,
        String hibernateDialect
    ) {

        private static RenderDatabaseDetails from(String rawUrl) {
            Logger log = LoggerFactory.getLogger(RenderDatabaseEnvironmentPostProcessor.class);
            try {
                log.info("Parsing database URL: {}", rawUrl.length() > 100 ? rawUrl.substring(0, 100) + "..." : rawUrl);
                
                // Handle both jdbc: and non-jdbc: formats
                String sanitized = rawUrl.startsWith("jdbc:") ? rawUrl.substring(5) : rawUrl;
                
                // Try to parse as URI
                URI uri;
                try {
                    uri = URI.create(sanitized);
                } catch (IllegalArgumentException e) {
                    log.error("Failed to parse DATABASE_URL as URI: {}", e.getMessage());
                    return null;
                }
                
                String scheme = uri.getScheme();
                if (!StringUtils.hasText(scheme)) {
                    log.error("No scheme found in DATABASE_URL");
                    return null;
                }

                boolean postgres = scheme.startsWith("postgres");
                boolean mysql = scheme.startsWith("mysql");
                if (!postgres && !mysql) {
                    log.error("Unsupported database scheme: {}", scheme);
                    return null;
                }

                String host = Objects.requireNonNullElse(uri.getHost(), "localhost");
                int port = uri.getPort();
                if (port < 0) {
                    port = postgres ? 5432 : 3306;
                    log.info("No port specified, using default: {}", port);
                }

                String path = uri.getPath();
                if (!StringUtils.hasText(path) || path.equals("/")) {
                    // Try to get database name from path or use default
                    path = postgres ? "/alertaid" : "/alertaid_db";
                    log.info("No database path specified, using default: {}", path);
                }

                StringBuilder jdbcUrl = new StringBuilder("jdbc:")
                    .append(postgres ? "postgresql" : "mysql")
                    .append("://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append(path);

                String query = uri.getQuery();
                if (StringUtils.hasText(query)) {
                    jdbcUrl.append("?").append(query);
                    // Ensure required MySQL parameters are present
                    if (!postgres && !query.contains("useSSL")) {
                        jdbcUrl.append("&useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                    }
                } else if (!postgres) {
                    // MySQL connection parameters for Render
                    jdbcUrl.append("?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8");
                } else {
                    // PostgreSQL connection parameters
                    jdbcUrl.append("?sslmode=require");
                }

                String userInfo = uri.getUserInfo();
                String username = null;
                String password = null;
                if (StringUtils.hasText(userInfo)) {
                    int separator = userInfo.indexOf(':');
                    if (separator >= 0) {
                        username = userInfo.substring(0, separator);
                        password = userInfo.substring(separator + 1);
                    } else {
                        username = userInfo;
                    }
                    log.info("Extracted username from connection string");
                } else {
                    log.warn("No user credentials found in DATABASE_URL");
                }

                String driver = postgres ? "org.postgresql.Driver" : "com.mysql.cj.jdbc.Driver";
                String dialect = postgres
                    ? "org.hibernate.dialect.PostgreSQLDialect"
                    : "org.hibernate.dialect.MySQLDialect";

                String finalJdbcUrl = jdbcUrl.toString();
                log.info("Constructed JDBC URL: {} (masked)", finalJdbcUrl.replaceAll("://[^:]+:[^@]+@", "://****:****@"));
                
                return new RenderDatabaseDetails(finalJdbcUrl, username, password, driver, dialect);
            } catch (Exception ex) {
                log.error("Error parsing DATABASE_URL: {}", ex.getMessage(), ex);
                return null;
            }
        }
    }
}
