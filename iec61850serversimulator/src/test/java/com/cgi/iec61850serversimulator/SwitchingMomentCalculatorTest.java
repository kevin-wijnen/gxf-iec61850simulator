package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwitchingMomentCalculatorTest {
	/**
	 * JUnit test class which tests the various related functions of the
	 * SwitchingMomentCalculator class.
	 *
	 * This includes unique functions for mocking a
	 * relay and schedule.
	 *
	 * @formatter:off
	 * Tests include:
	 * - Checking the mocked relay
	 * - Checking the mocked schedule
	 * - Checking the generated Switching Moment made out of a mocked relay and schedule
	 * @formatter:on
	 */

	private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculatorTest.class);

	// Initializing test Device
	Device device = new Device();

	@Test
	public void checkMockRelay() {
		// Mock Relay test
		int relayNr = 1;

		Relay relay = this.getMockRelay(relayNr - 1, null);
		assertEquals(relay.getIndexNumber(), 1);
	}

	@Test
	public void checkMockSchedule() {
		// Mock Schedule test
		int scheduleNr = 1;
		int relayNr = 1;
		// Trigger day(s), see Enum in GXF LF Energy documentation
		// 1 = Every day
		int dayInt = 0;
		// General time type for both on and off times
		// 0 = fixed time, 1 = light sensor, 2 = astronomical time
		// -1 = disabled action
		int fixedTimeInt = 0;
		// Optional: Specific time type for on time (-1 = not used)
		int fixedTimeOn = -1;
		// Optional: Specific time type for off time (-1 = not used)
		int fixedTimeOff = -1;
		LocalTime timeOn = LocalTime.of(12, 00);
		LocalTime timeOff = LocalTime.of(13, 00);
		int burningMinutes = 30;

		Schedule schedule = this.getMockSchedule(scheduleNr - 1, relayNr, dayInt, fixedTimeInt, fixedTimeOn,
				fixedTimeOff, timeOn, timeOff, burningMinutes);

		assertEquals(scheduleNr, schedule.getIndexNumber());
		assertEquals(relayNr, schedule.getRelayNr());
		assertEquals(fixedTimeInt, schedule.getTimeOnTypeInt());
		assertEquals(fixedTimeInt, schedule.getTimeOffTypeInt());
		assertEquals(timeOn, schedule.getTimeOn());
		assertEquals(timeOff, schedule.getTimeOff());
		assertEquals(burningMinutes, schedule.getBurningMinsOn());

	}

	@Test
	public void calculateSwitchingMoments() {

		int scheduleNr = 1;
		int relayNr = 1;
		// Trigger day(s), see Enum in GXF LF Energy documentation
		// 1 = Every day
		int dayInt = 0;
		// General time type for both on and off times
		int fixedTimeInt = 0;
		// Optional: Specific time type for on time
		// -1 = using general time type
		int fixedTimeOn = -1;
		// Optional: Specific time type for off time
		// -1 = using general time type
		int fixedTimeOff = -1;
		LocalTime timeOn = LocalTime.of(12, 00);
		LocalTime timeOff = LocalTime.of(13, 00);
		int burningMinutes = 30;
		// Optional: Custom schedule
		Schedule customSchedule = this.getMockSchedule(scheduleNr - 1, relayNr, dayInt, fixedTimeInt, fixedTimeOn,
				fixedTimeOff, timeOn, timeOff, burningMinutes);
		boolean useCustomSchedule = false;

		// Initializing mock relay
		if (!useCustomSchedule) {
			Relay relay = this.getMockRelay(relayNr, null);
			Relay[] relays = new Relay[1];
			relays[0] = relay;
			this.device.setRelays(relays);
		} else {
			Relay relay = this.getMockRelay(relayNr, customSchedule);
			Relay[] relays = new Relay[1];
			relays[0] = relay;
			this.device.setRelays(relays);
		}

		// Calculating switching moment

		// Tests
		// TODO: Check the SwitchingMoments

	}

	public Relay getMockRelay(int relayNr, Schedule customSchedule) {
		Relay relay = new Relay(relayNr);

		Schedule[] schedules = new Schedule[1];

		// Making mock fixed time schedule.

		if (customSchedule != null) {
			// Default mock schedule:
			// On time: 12 PM
			// Off time: 1 PM
			// Burning Minutes: 30
			Schedule schedule = this.getMockLunchTimeSchedule(1);
			schedules[0] = schedule;
		}

		relay.setSchedules(schedules);

		return relay;
	}

	public Schedule getMockLunchTimeSchedule(int scheduleNr) {

		Schedule schedule = new Schedule(scheduleNr);

		int relayNr = 1;
		// Trigger day(s), see Enum in GXF LF Energy documentation
		// 0 = Every day
		int dayInt = 0;
		// General time type for both on and off times
		// 0 = fixed time, 1 = light sensor, 2 = astronomical time
		int fixedTimeInt = 0;
		// Optional: Specific time type for on time
		// -1 = using general time type
		int fixedTimeOn = -1;
		// Optional: Specific time type for off time
		// -1 = using general time type
		int fixedTimeOff = -1;
		LocalTime timeOn = LocalTime.of(12, 00);
		LocalTime timeOff = LocalTime.of(13, 00);
		int burningMinutes = 30;

		schedule.setRelayNr(relayNr);
		schedule.setDayInt(dayInt);
		schedule.setTimeOn(timeOn);
		schedule.setTimeOff(timeOff);
		if (fixedTimeOn > 0 || fixedTimeOff > 0) {
			schedule.setTimeOnTypeInt(fixedTimeInt);
			schedule.setTimeOffTypeInt(fixedTimeInt);
		} else {
			schedule.setTimeOnTypeInt(fixedTimeOn);
			schedule.setTimeOffTypeInt(fixedTimeOff);
		}
		schedule.setBurningMinsOn(burningMinutes);

		return schedule;
	}

	public Schedule getMockSchedule(int scheduleNr, int relayNr, int dayInt, int fixedTimeInt, int fixedTimeOn,
			int fixedTimeOff, LocalTime timeOn, LocalTime timeOff, int burningMinutes) {
		Schedule schedule = new Schedule(scheduleNr);

		schedule.setRelayNr(relayNr);
		// Day 0: Everyday of the week
		schedule.setDayInt(dayInt);
		schedule.setTimeOn(timeOn);
		// Time On Type 0: Fixed time
		schedule.setTimeOnTypeInt(0);
		schedule.setTimeOff(timeOff);
		// Time Off Type 0: Fixed time
		schedule.setTimeOffTypeInt(0);
		schedule.setBurningMinsOn(burningMinutes);

		return schedule;
	}
}
