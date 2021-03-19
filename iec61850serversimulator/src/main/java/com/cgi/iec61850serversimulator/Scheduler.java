package com.cgi.iec61850serversimulator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which schedules autonomous switching moments for future activation.
 */
public class Scheduler {

	private ScheduledThreadPoolExecutor executor;
	private List<ScheduledFuture<?>> scheduledFutures;
	private TimeCalculator timeCalculator;
	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	private Device device;

	public Scheduler(Device device) {
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

		// Should remove cancelled futures, still included .purge when calculating new
		// switching moments
		executor.setRemoveOnCancelPolicy(true);
		this.timeCalculator = new TimeCalculator();
		device = this.device;
	}

	// Base it on feature/example-scheduling code! Triggering at certain
	// times instead of a continuous check!

	// TODO: Build switching moment calculation functionality
	// Read schedules from device's relays (1 * 4 * 50)
	// Checks on and off times along with burning minutes to see which switching
	// moments should be made for X hours
	// Scheduling said tasks by calculating the relative time and using said
	// relative time to set the task with ScheduledExecutorService from Java

	public void schedulingTasks(List<SwitchingMoment> switchingMoments) {
		// Check future if it is empty. If it is not empty, then cancel > purge > clear
		// it.
		// Calculate relative time for each task
		// Schedule it, with future put into list
		this.futureCheck(this.scheduledFutures);
		for (int i = 0; i < switchingMoments.size(); i++) {
			SwitchingMoment switchingMoment = switchingMoments.get(i);
			LocalDateTime currentTime = LocalDateTime.now();
			// Calculate relative time
			int relativeTime = this.timeCalculator.calculateRelativeTime(switchingMoment, currentTime);
			int relayNr = switchingMoment.getRelayNr();
			if (switchingMoment.isTriggerAction()) {
				Runnable runOn = this.onRunnableCreator(relayNr);
				this.executor.schedule(runOn, relativeTime, TimeUnit.SECONDS);
			} else if (!switchingMoment.isTriggerAction()) {
				Runnable runOff = this.offRunnableCreator(relayNr);
				this.executor.schedule(runOff, relativeTime, TimeUnit.SECONDS);
			}

		}

	}

	public void switchingMomentCalculation(final Device device) {
		logger.info("Switching moment calculation feature yet to implement.");

		// Done in branch feature-78, with the SwitchingMomentCalculator class

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

		// Done by TimeCalculator.calculateRelativeTime instead
	}

	// Runnable to turn on relay light
	private Runnable onRunnableCreator(final int relayNr) {
		Runnable onRun = new Runnable() {
			@Override
			public void run() {
				Scheduler.this.device.getRelay(relayNr).setLight(true);
			}
		};
		return onRun;
	}

	// Runnable to turn off relay light
	private Runnable offRunnableCreator(final int relayNr) {
		Runnable offRun = new Runnable() {
			@Override
			public void run() {
				Scheduler.this.device.getRelay(relayNr).setLight(false);
			}
		};
		return offRun;
	}

	private void futureCheck(List<ScheduledFuture<?>> scheduledFutures) {
		if (!scheduledFutures.isEmpty()) {
			for (int i = 0; i < scheduledFutures.size(); i++) {
				ScheduledFuture<?> future = scheduledFutures.get(i);
				if (!future.isDone()) {
					future.cancel(true);
				}
			}
			scheduledFutures.clear();
			// To prevent memory leak: Purging executor's queue
			this.executor.purge();
		}
	}
}
