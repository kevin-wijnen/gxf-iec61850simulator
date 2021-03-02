package com.cgi.iec61850serversimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

	// TODO: Steps
	// @formatter:off
	// Initializing Scheduler during boot
	// Call calculateSwitchingMoments function with Device input to get Relay's schedules from, during boot and schedule changes!
	// Determine actions to determine SwitchingMoments within X hours (24?), by checking Time On and Time Off, and BurningMinutes?
	// When action is applicable --> making SwitchingMoment object with Trigger's Time, Trigger's Action, Relay Number
	// For every SwitchingMoment: Calculate relative time from trigger time and schedule said action
	//
	// For schedule changes: Clear Future of the ScheduledExecutorService to clear tasks!
	// @formatter:on

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTest.class);

	// Initializing Scheduler components
	final ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
	final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
	// Scheduler scheduler = new Scheduler(this.taskScheduler);

	// Function: calculateSwitchingMoments
	// Input: Device (to get the schedules from)
	// Output: List of SwitchingMoments based on the schedules' actions and times

	public List<SwitchingMoment> calculateSwitchingMoment(Device device) {
		List<SwitchingMoment> switchingMoments = new ArrayList<SwitchingMoment>();

		// For all relays, get the schedules with for-loop
		// Determine if schedule is enabled
		// Determine if Time On and Time Off are compatible
		// Make SwitchingMoment, add to relay
		return null;
	}

	// Function: calculateRelativeTime
	// Input: SwitchingMoment (to get the LocalDateTime of the action from
	// Output: Calculated Int relativeTime to function as delay for the Runnable

	public int calulateRelativeTime(SwitchingMoment switchingMoment) {
		// Read LocalDateTime from TriggerTime
		// Calculate relative time from TriggerTime to now
		// Give the relative time as Int for the scheduler
		return 0;
	}

	public void scheduleSwitchingMoments(List<SwitchingMoment> switchingMoments) {
		// In for loop:
		// Calculate relative time from the TriggerTime by calling calculateRelativeTime
		// Activate runnable with extracted RelayNr, and the relativeTime as delay for
		// the On/Off action (also determined from the SwitchingMoment

	}
}
