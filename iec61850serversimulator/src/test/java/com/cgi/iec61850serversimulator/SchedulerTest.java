package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class SchedulerTest {

	// Initializing example schedule + scheduler class
	Scheduler scheduler = new Scheduler();

	@Test
	void extractTime() {
		Schedule schedule1 = new Schedule(0);

		schedule1.setIndexNumber(1);
		schedule1.setRelayNr(1);
		schedule1.setEnabled(true);
		schedule1.setDescription("Test schedule");
		schedule1.setDayInt(0);
		schedule1.setTimeOn(LocalTime.NOON);
		schedule1.setTimeOnTypeInt(0);
		schedule1.setTimeOff(LocalTime.of(13, 00));
		schedule1.setTimeOffTypeInt(0);
		schedule1.setBurningMinsOn(0);
		schedule1.setBeforeOffset(0);
		schedule1.setAfterOffset(0);

		LocalTime[] time = this.scheduler.timeExtractor(schedule1);

		// Start and stop time validation
		assertEquals(LocalTime.of(12, 00), time[0]);
	}
	// Initializing example schedule

	@Test
	void triggerSchedule() {
		Schedule schedule1 = new Schedule(0);
		Device device = new Device();

		device.initalizeDevice(null);

		schedule1.setIndexNumber(1);
		schedule1.setRelayNr(1);

		schedule1.setEnabled(true);
		schedule1.setDescription("Test schedule");
		schedule1.setDayInt(0);
		schedule1.setTimeOn(LocalTime.NOON);
		schedule1.setTimeOnTypeInt(0);

		schedule1.setTimeOff(LocalTime.of(13, 00));
		schedule1.setTimeOffTypeInt(0);
		schedule1.setBurningMinsOn(0);
		schedule1.setBeforeOffset(0);
		schedule1.setAfterOffset(0);

		LocalTime toCheckTime = LocalTime.of(12, 00);

		// Current Time
		this.scheduler.scheduleCheck(device, schedule1, 1);
		assertFalse(device.getRelay(1).getLight());

		this.scheduler.scheduleCheck(device, schedule1, toCheckTime, 1);
		assertTrue(device.getRelay(1).getLight());

	}

}
