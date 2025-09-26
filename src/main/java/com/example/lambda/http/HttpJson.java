package com.example.lambda.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

@Component
public class HttpJson {
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int maxAttempts;
    private final Duration baseDelay;

    public HttpJson(com.example.lambda.config.AppConfig.Config cfg) {
        this.client = HttpClient.newBuilder().connectTimeout(cfg.httpTimeout()).build();
        this.maxAttempts = Math.max(1, cfg.retryMaxAttempts());
        this.baseDelay = cfg.retryBaseDelay();
    }

    public JsonNode get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "nsw-address-lookup/1.0")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                int sc = res.statusCode();
                if (sc >= 200 && sc < 300) {
                    return mapper.readTree(res.body());
                }
                if (retryable(sc) && attempt < maxAttempts) {
                    backoff(attempt);
                    continue;
                }
                throw new RuntimeException("HTTP " + sc + " from upstream");
            } catch (java.io.IOException | InterruptedException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                if (attempt < maxAttempts) {
                    backoff(attempt);
                    continue;
                }
                throw e;
            }
        }
    }

    private static boolean retryable(int sc) {
        return sc == 429 || (sc >= 500 && sc < 600);
    }

    private void backoff(int attempt) {
        try {
            long ms = (long) (baseDelay.toMillis() * Math.pow(2, attempt - 1));
            Thread.sleep(Math.min(ms, 2000));
        } catch (InterruptedException ignored) {}
    }
}
