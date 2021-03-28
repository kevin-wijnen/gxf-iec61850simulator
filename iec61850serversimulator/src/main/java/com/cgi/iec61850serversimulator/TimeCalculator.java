package com.cgi.iec61850serversimulator;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeCalculator {
    public static int calculateSecondsUntil(LocalDateTime from, LocalDateTime until) {
        Duration relativeTime = Duration.between(from, until);

        return (int) relativeTime.getSeconds();

    }
}
