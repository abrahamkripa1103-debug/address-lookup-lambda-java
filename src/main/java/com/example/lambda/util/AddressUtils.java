package com.example.lambda.util;

import org.springframework.stereotype.Component;

@Component
public class AddressUtils {
    public String normalizeForArcGis(String raw) {
        if (raw == null) return "";
        String upper = raw.toUpperCase().trim().replaceAll("\\s+", " ");
        return upper.replace("'", "''");
    }

    public void validateAddress(String a) {
        if (a == null || a.isBlank()) throw new IllegalArgumentException("address is required");
        if (a.length() > 200) throw new IllegalArgumentException("address too long");
        if (!a.toUpperCase().matches("[A-Z0-9 .,'/-]+")) throw new IllegalArgumentException("address has invalid characters");
    }
}
