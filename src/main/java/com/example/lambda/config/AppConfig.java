package com.example.lambda.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

@Configuration
public class AppConfig {

    public record Config(
        String addressLayerQuery,
        String suburbLayerQuery,
        String sedLayerQuery,
        Duration httpTimeout,
        int retryMaxAttempts,
        Duration retryBaseDelay
    ) {}

    @Bean
    public Config config() {
        Config defaults = new Config(
            "https://portal.spatial.nsw.gov.au/server/rest/services/NSW_Geocoded_Addressing_Theme/FeatureServer/1/query",
            "https://portal.spatial.nsw.gov.au/server/rest/services/NSW_Administrative_Boundaries_Theme/FeatureServer/2/query",
            "https://portal.spatial.nsw.gov.au/server/rest/services/NSW_Administrative_Boundaries_Theme/FeatureServer/4/query",
            Duration.ofSeconds(8), 2, Duration.ofMillis(200)
        );

        Config file = loadYaml("config/app.yaml")
                .or(() -> loadYaml("/opt/config/app.yaml"))
                .orElse(null);

        String addr = envOr("ADDR_QUERY", or(file, defaults, Config::addressLayerQuery));
        String sub  = envOr("SUBURB_QUERY", or(file, defaults, Config::suburbLayerQuery));
        String sed  = envOr("SED_QUERY", or(file, defaults, Config::sedLayerQuery));

        Duration timeout = Duration.ofMillis(Long.parseLong(
            envOr("HTTP_TIMEOUT_MS", String.valueOf(or(file, defaults, Config::httpTimeout).toMillis()))
        ));
        int attempts = Integer.parseInt(envOr("RETRY_MAX_ATTEMPTS",
            String.valueOf(or(file, defaults, Config::retryMaxAttempts))));
        Duration baseDelay = Duration.ofMillis(Long.parseLong(
            envOr("RETRY_BASE_DELAY_MS", String.valueOf(or(file, defaults, Config::retryBaseDelay).toMillis()))
        ));

        return new Config(addr, sub, sed, timeout, attempts, baseDelay);
    }

    private static Optional<Config> loadYaml(String path) {
        try {
            Path p = Path.of(path);
            if (!Files.exists(p)) return Optional.empty();
            var om = new ObjectMapper(new YAMLFactory());
            try (InputStream in = Files.newInputStream(p)) {
                return Optional.of(om.readValue(in, Config.class));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String envOr(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? def : v;
    }

    private static <T> T or(Config a, Config b, java.util.function.Function<Config, T> getter) {
        return a != null ? getter.apply(a) : getter.apply(b);
    }
}
