package com.cgi.iec61850serversimulator;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeCalculator {
	public int calculateRelativeTime(SwitchingMoment switchingMoment, LocalDateTime scheduledTime) {
		LocalDateTime triggerTime = switchingMoment.getTriggerTime();
		Duration relativeTime = Duration.between(scheduledTime, triggerTime);

		return (int) relativeTime.getSeconds();

	}
}
