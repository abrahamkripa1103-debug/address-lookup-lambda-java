package com.example.lambda.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AddressUtilsTest {

    @Test
    void normalize_uppercasesAndCollapsesSpaces() {
        AddressUtils u = new AddressUtils();
        String out = u.normalizeForArcGis(" 346  Panorama  Avenue  Bathurst ");
        assertThat(out).isEqualTo("346 PANORAMA AVENUE BATHURST");
    }

    @Test
    void normalize_escapesSingleQuotes() {
        AddressUtils u = new AddressUtils();
        String out = u.normalizeForArcGis("O'CONNOR ST");
        assertThat(out).isEqualTo("O''CONNOR ST");
    }

    @Test
    void validate_rejectsTooLong() {
        AddressUtils u = new AddressUtils();
        String longStr = "A".repeat(201);
        assertThatThrownBy(() -> u.validateAddress(longStr))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_rejectsInvalidChars() {
        AddressUtils u = new AddressUtils();
        assertThatThrownBy(() -> u.validateAddress("DROP TABLE;"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}