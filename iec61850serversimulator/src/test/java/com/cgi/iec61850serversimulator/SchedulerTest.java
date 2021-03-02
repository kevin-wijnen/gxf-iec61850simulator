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

class SchedulerTest {
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

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTest.class);

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

	public void mockDeviceScheduler() {
		// Mocking Device for Unit Tests Scheduler
		// Relay + Schedule
		// Second relay with first schedule
		// <initialize 4 Relays>
		// <initialize Schedule within 2nd relay

		Relay[] relays = new Relay[4];

		// For-loop to initialize the mock relays
		for (int i = 0; i < 4; i++) {
			relays[i] = new Relay(i + 1);
		}

		Schedule[] schedule = new Schedule[1];
		schedule[0] = new Schedule(0);

		this.device.setRelays(relays);
		logger.info(relays.toString());
		relays[0].setLight(true);
		relays[0].setSchedules(schedule);
		logger.info(this.device.getRelay(1).toString());

	}

	public void mockFixedTimeScheduleScheduler(int indexNumber, int relayNr, int dayInt, LocalTime timeOn,
			LocalTime timeOff, int burningMinutes) {
		// TODO: Add other types of Schedules (Astronomic times use the srBef/-AftWd
		// variables), and have a TimeType of 1

		Schedule schedule = this.device.getRelay(1).getSchedule(1);
		// Enabled fixed time schedule of Relay 2
		schedule.setIndexNumber(indexNumber);
		schedule.setRelayNr(relayNr);
		schedule.setEnabled(true);
		schedule.setDayInt(dayInt);
		schedule.setTimeOn(timeOn);
		schedule.setTimeOnTypeInt(0);
		schedule.setTimeOff(timeOff);
		schedule.setTimeOffTypeInt(0);
		schedule.setBurningMinsOn(burningMinutes);
	}

	@Test
	public void calculateSwitchingMoments() {
		this.mockDeviceScheduler();
		this.mockFixedTimeScheduleScheduler(0, 1, 0, LocalTime.of(12, 00), LocalTime.of(13, 00), 30);
		// TODO: Fill in!
		assertNotNull(this.device.getRelay(1).getSchedule(1));

		// Scheduler tasks:
		// Checking schedules of device
		// Calculating switching moments for X amount of hours. 24?

		// Result: Calculated moments in Scheduled Future

		// To test:
		// Check for correct calculation
	}

}
