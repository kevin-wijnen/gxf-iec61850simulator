package com.cgi.iec61850serversimulator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which schedules autonomous switching moments for future activation.
 */
public class Scheduler {
	// Base it on feature/example-scheduling code! Triggering at certain
	// times instead of a continuous check!

	// TODO: Build switching moment calculation functionality
	// Read schedules from device's relays (1 * 4 * 50)
	// Checks on and off times along with burning minutes to see which switching
	// moments should be made for X hours
	// Scheduling said tasks by calculating the relative time and using said
	// relative time to set the task with ScheduledExecutorService from Java

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	// Callables declareren ivm ScheduledFuture
	// Of toch runnables?

	// private TaskScheduler switchingMomentScheduler;
	// private ScheduledFuture<?> future;

	/*
	 * public ScheduledFuture<?> getFuture() { return this.future; }
	 *
	 * public void setFuture(ScheduledFuture<?> future) { this.future = future; }
	 *
	 * public Scheduler(final TaskScheduler scheduler) {
	 * this.switchingMomentScheduler = scheduler; }
	 */

	public void switchingMomentCalculation(final Device device) {
		logger.info("Switching moment calculation feature yet to implement.");

		/* @formatter:off
		 * TODO: Switching moment calculation using schedules from device...
		 *
		 * Steps:
		 * - Reading schedules from device's relays
		 * - Determine from each schedule which switching moments are valid ((TimeOff - TimeOn) > burningMins);
		 * - Either add converted relative time, or Switching Moment objects to array;
		 * - (Use switchingMomentRelativeTimeConversion to convert from Switching Moment object
		 * 	 to relative time;)
		 * - Use relative times to schedule tasks (using ScheduledExecutorService
		 *   or different process?)
		 * @formatter:on
		 */
	}

	public void switchingMomentRelativeTimeConversion() {
		// TODO: Switching moment --> relative time conversion for scheduled trigger
		// actions
	}

	// Voorbeeld voor On task
	private Runnable onRunnableCreator(final int relayNr) {
		Runnable onRun = new Runnable() {
			@Override
			public void run() {
				// functie hier met parameter
				Scheduler.this.switchingMomentRelativeTimeConversion();
			}
		};
		return onRun;
	}

	// Voorbeeld voor Off task
	private Runnable offRunnableCreator(final int relayNr) {
		Runnable offRun = new Runnable() {
			@Override
			public void run() {
				// functie hier met parameter
				Scheduler.this.switchingMomentRelativeTimeConversion();
			}
		};
		return offRun;
	}
}
