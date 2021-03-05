package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

class SwitchingMomentCalculatorTest {
	// TODO:
	// Triggeren per wijziging?
	// TaskScheduler aanroepen
	// ScheduledFuture aanroepen
	// Initialiseren van class met de TaskScheduler
	// Berekenen schakelmomenten, einde

	// Bij verandering in schema (met bool)
	// bijhouden, dan opniuew laten berekenen

	// Baseer het op het ontwerp van feature/example-scheduling!
	// Initializing example schedule + scheduler class

	private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculatorTest.class);

	// Initializing Scheduler components
	final ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
	final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
	// Scheduler scheduler = new Scheduler(this.taskScheduler);

	// Initializing test Device
	Device device = new Device();
	// Invullen test data...
	// Via Setters doen met objecten, alleen benodigde data invullen
	// Alleen 1 Relay met 1 Schema
	// In plaats van door hele ServerSap en -Wrapper proces te gaan, kijken naar
	// handmatige invullen via setters
	// Nodig: Clock, Relays, 50 Schedules per relay

	@Test
	public void calculateSwitchingMoments() {
		Relay relay = getMockedRelay(1);
		Relay[] relays = new Relay[1];
		relays[0] = relay;
		this.device.setRelays(relays);

		// TODO: Fill in!
		assertNotNull(this.device.getRelay(1).getSchedule(1));

		// Scheduler tasks:
		// Checking schedules of device
		// Calculating switching moments for X amount of hours. 24?

		// Result: Calculated moments in Scheduled Future

		// To test:
		// Check for correct calculation
	}

	private Relay getMockedRelay(int relayNr) {
		// Mocking Device for Unit Tests Scheduler
		// Relay + Schedule
		// Second relay with first schedule
		// <initialize Schedule for the relay
		Relay relay = new Relay(relayNr);
		relay.setLight(true);

		Schedule schedule = this.getMockedSchedule(0, 1, 0, LocalTime.of(12, 00), LocalTime.of(13, 00), 30);

		Schedule[] schedules = new Schedule[1];
		schedules[0] = schedule;
		relay.setSchedules(schedules);

		logger.info(relay.toString());

		return relay;
	}

	private Schedule getMockedSchedule(int indexNumber, int relayNr, int dayInt, LocalTime timeOn,
									  LocalTime timeOff, int burningMinutes) {

		Schedule schedule = new Schedule(indexNumber);
		// Enabled fixed time schedule of Relay 2
		schedule.setRelayNr(relayNr);
		schedule.setEnabled(true);
		schedule.setDayInt(dayInt);
		schedule.setTimeOn(timeOn);
		// Time On type set to Fixed Time
		schedule.setTimeOnTypeInt(0);
		schedule.setTimeOff(timeOff);
		// Time Off type set to Fixed Time
		schedule.setTimeOffTypeInt(0);
		schedule.setBurningMinsOn(burningMinutes);

		return schedule;
	}

}
