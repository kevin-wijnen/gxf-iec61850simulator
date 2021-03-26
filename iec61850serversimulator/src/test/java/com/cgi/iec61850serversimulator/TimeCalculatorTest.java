package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test class which tests the time calculation function.
 */
public class TimeCalculatorTest {

    private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculatorTest.class);

    @Test
    public void calculateRemainingSeconds() {

        LocalDateTime triggerTime = LocalDateTime.of(2010, 1, 1, 12, 00);
        LocalDateTime testTime = LocalDateTime.of(2010, 1, 1, 11, 58);

        // Creating SwitchingMoment with Relay 1 and an On action
        SwitchingMoment switchingMoment = new SwitchingMoment(1, triggerTime, true);

        int remainingSeconds = TimeCalculator.calculateSecondsUntil(triggerTime, testTime);
        assertEquals(remainingSeconds, 120);

    }

}
