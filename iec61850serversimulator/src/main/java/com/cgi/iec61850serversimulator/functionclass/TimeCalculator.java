package com.cgi.iec61850serversimulator.functionclass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculator {
    public static int calculateSecondsUntil(LocalDateTime from, LocalDateTime until) {
        Duration relativeTime = Duration.between(from, until);

        return (int) relativeTime.getSeconds();

    }

    public static int localTimeToHoursMinutesInt(LocalTime time) {
        return time.getHour() * 100 + time.getMinute();
    }
}
