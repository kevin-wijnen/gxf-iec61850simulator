package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.Schedule.ScheduleBuilder;

class SwitchingMomentCalculatorTest {
	/**
	 * JUnit test class which tests the various related functions of the
	 * SwitchingMomentCalculator class.
	 *
	 * This includes unique functions for mocking a relay and schedule.
	 *
	 * Tests include:
	 * <ul>
	 * <li>Checking the mocked relay
	 * <li>Checking the mocked schedule
	 * <li>Checking the generated Switching Moment made out of a mocked relay and
	 * schedule
	 * </ul>
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
		// 0 = Every day
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
		boolean enabled = true;

		Schedule schedule = new ScheduleBuilder(scheduleNr - 1).relayNr(relayNr).dayInt(dayInt)
				.fixedTimeInt(fixedTimeInt).fixedTimeOn(fixedTimeOn).fixedTimeOff(fixedTimeOff)
				.timeOn(timeOn).timeOff(timeOff).burningMins(burningMinutes).isEnabled(enabled).buildSchedule();

		// Schedule schedule = this.getMockSchedule(scheduleNr - 1, relayNr, dayInt,
		// fixedTimeInt, fixedTimeOn,
		// fixedTimeOff, timeOn, timeOff, burningMinutes);

		assertEquals(scheduleNr, schedule.getIndexNumber());
		assertEquals(relayNr, schedule.getRelayNr());
		assertEquals(fixedTimeInt, schedule.getTimeOnTypeInt());
		assertEquals(fixedTimeInt, schedule.getTimeOffTypeInt());
		assertEquals(timeOn, schedule.getTimeOn());
		assertEquals(timeOff, schedule.getTimeOff());
		assertEquals(burningMinutes, schedule.getBurningMinsOn());
		assertEquals(enabled, schedule.isEnabled());

	}

	@Test
	public void calculateEveryDaySwitchingMoments() throws SwitchingMomentCalculationException {

		int scheduleNr = 1;
		int relayNr = 1;
		// Trigger day(s), see Enum in GXF LF Energy documentation
		// 0 = Every day
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
		boolean enabled = true;

		// Optional: Custom schedule
		Schedule customSchedule = new ScheduleBuilder(scheduleNr - 1).relayNr(relayNr).dayInt(dayInt)
				.fixedTimeInt(fixedTimeInt).fixedTimeOn(fixedTimeOn).fixedTimeOff(fixedTimeOff)
				.timeOn(timeOn).timeOff(timeOff).burningMins(burningMinutes).isEnabled(enabled).buildSchedule();
		boolean useCustomSchedule = false;

		// Initializing mock relay
		// Custom schedule separated test!
		Schedule schedule = this.getMockLunchTimeSchedule(scheduleNr);
		Relay relay = this.getMockRelay(relayNr, schedule);
		Relay[] relays = new Relay[1];
		relays[0] = relay;
		this.device.setRelays(relays);

		// Calculating switching moment

		SwitchingMomentCalculator calculator = new SwitchingMomentCalculator();
		List<SwitchingMoment> switchingMoments = calculator.calculateSwitchingMoments(this.device);

		// Tests

		SwitchingMoment actualSwitchingMomentOn = switchingMoments.get(0);
		SwitchingMoment actualSwitchingMomentOff = switchingMoments.get(2);
		SwitchingMoment actualSwitchingMomentNextDayOn = switchingMoments.get(1);
		SwitchingMoment actualSwitchingMomentNextDayOff = switchingMoments.get(3);

		LocalDateTime expectedLunchTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).withHour(12);
		assertEquals(expectedLunchTime, actualSwitchingMomentOn.getTriggerTime());
		assertTrue(actualSwitchingMomentOn.isTriggerAction());

		LocalDateTime expectedAfterLunchTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).withHour(13);
		assertEquals(expectedAfterLunchTime, actualSwitchingMomentOff.getTriggerTime());
		assertFalse(actualSwitchingMomentOff.isTriggerAction());

		LocalDateTime expectedNextLunchTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS)
				.withHour(12);
		assertEquals(expectedNextLunchTime, actualSwitchingMomentNextDayOn.getTriggerTime());
		assertTrue(actualSwitchingMomentOn.isTriggerAction());

		LocalDateTime expectedNextAfterLunchTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS)
				.withHour(13);
		assertEquals(expectedNextAfterLunchTime, actualSwitchingMomentNextDayOff.getTriggerTime());
		assertFalse(actualSwitchingMomentOff.isTriggerAction());

	}

	@Test
	public void calculateCustomScheduleSwitchingMoments() throws SwitchingMomentCalculationException {

		// Custom schedule
		// Set up your own schedule with the variables

		int scheduleNr = 1;
		int relayNr = 1;
		// Trigger day(s), see Enum in GXF LF Energy documentation
		// 0 = Every day
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
		LocalTime timeOff = LocalTime.of(23, 33);
		int burningMinutes = 30;
		boolean enabled = true;

		// Custom schedule initialization
		Schedule customSchedule = new ScheduleBuilder(scheduleNr - 1).relayNr(relayNr).dayInt(dayInt)
				.fixedTimeInt(fixedTimeInt).fixedTimeOn(fixedTimeOn).fixedTimeOff(fixedTimeOff)
				.timeOn(timeOn).timeOff(timeOff).burningMins(burningMinutes).isEnabled(enabled).buildSchedule();

		// Flag to enable the custom schedule
		boolean useCustomSchedule = true;

		// Initializing mock relay

		Relay relay = this.getMockRelay(relayNr, customSchedule);
		Relay[] relays = new Relay[1];
		relays[0] = relay;
		this.device.setRelays(relays);

		// Calculating switching moment

		SwitchingMomentCalculator calculator = new SwitchingMomentCalculator();
		List<SwitchingMoment> switchingMoments = calculator.calculateSwitchingMoments(this.device);

		// Tests

		SwitchingMoment actualSwitchingMomentOn = switchingMoments.get(0);
		SwitchingMoment actualSwitchingMomentOff = switchingMoments.get(2);
		SwitchingMoment actualSwitchingMomentNextDayOn = switchingMoments.get(1);
		SwitchingMoment actualSwitchingMomentNextDayOff = switchingMoments.get(3);

		LocalDateTime expectedTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).withHour(timeOn.getHour())
				.withMinute(timeOn.getMinute());
		assertEquals(expectedTime, actualSwitchingMomentOn.getTriggerTime());
		assertTrue(actualSwitchingMomentOn.isTriggerAction());

		LocalDateTime expectedAfterTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
				.withHour(timeOff.getHour()).withMinute(timeOff.getMinute());
		assertEquals(expectedAfterTime, actualSwitchingMomentOff.getTriggerTime());
		assertFalse(actualSwitchingMomentOff.isTriggerAction());

		LocalDateTime expectedNextTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES)
				.withHour(timeOn.getHour()).withMinute(timeOn.getMinute());
		assertEquals(expectedNextTime, actualSwitchingMomentNextDayOn.getTriggerTime());
		assertTrue(actualSwitchingMomentOn.isTriggerAction());

		LocalDateTime expectedNextAfterTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES)
				.withHour(timeOff.getHour()).withMinute(timeOff.getMinute());
		assertEquals(expectedNextAfterTime, actualSwitchingMomentNextDayOff.getTriggerTime());
		assertFalse(actualSwitchingMomentOff.isTriggerAction());

	}

	public Relay getMockRelay(int relayNr, Schedule customSchedule) {
		Relay relay = new Relay(relayNr);

		Schedule[] schedules = new Schedule[1];

		// Making mock fixed time schedule.

		if (customSchedule == null) {
			// Default mock schedule:
			// On time: 12 PM
			// Off time: 1 PM
			// Burning Minutes: 30
			Schedule schedule = this.getMockLunchTimeSchedule(1);
			schedules[0] = schedule;
		}

		else {
			schedules[0] = customSchedule;
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
		if (fixedTimeOn < 0 || fixedTimeOff < 0) {
			schedule.setTimeOnTypeInt(fixedTimeInt);
			schedule.setTimeOffTypeInt(fixedTimeInt);
		} else {
			schedule.setTimeOnTypeInt(fixedTimeOn);
			schedule.setTimeOffTypeInt(fixedTimeOff);
		}
		schedule.setBurningMinsOn(burningMinutes);
		schedule.setEnabled(true);

		return schedule;
	}
}
