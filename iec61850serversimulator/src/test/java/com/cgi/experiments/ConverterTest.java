package com.cgi.experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class ConverterTest {

    @Test
    void bytesEpochConverterTest() {
        // 8 byte timestamp is Epoch second timestamp, originating from
        // BdaTimestamp
        final byte[] bytesEpochTime = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        final ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
        final long longTime = wrappedTime.getLong();
        final LocalDateTime wantedTime = LocalDateTime.of(1970, Month.JANUARY, 01, 01, 00, 00);
        final LocalDateTime epochTimeCET = LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1));
        assertEquals(epochTimeCET, wantedTime);

    }
}