package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * JUnit test class which tests the time calculation function.
 */
public class TimeCalculatorTest {

    @Test
    public void calculateRemainingSeconds() {

        LocalDateTime triggerTime = LocalDateTime.of(2010, 1, 1, 12, 00);
        LocalDateTime testTime = LocalDateTime.of(2010, 1, 1, 11, 58);

        int remainingSeconds = TimeCalculator.calculateSecondsUntil(testTime, triggerTime);
        assertEquals(remainingSeconds, 120);

    }

}
