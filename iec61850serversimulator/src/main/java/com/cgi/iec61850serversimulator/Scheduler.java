package com.cgi.iec61850serversimulator;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

/**
 * Class which schedules autonomous switching moments for future activation.
 */
public class Scheduler {
	// Base it on feature/example-scheduling code! Triggering at certain
	// times instead of a continuous check!

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

	//
	private int CALCULATION_DELAY = 5;
	private TaskScheduler scheduler;
	private ScheduledFuture<?> future;

	public ScheduledFuture<?> getFuture() {
		return this.future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public Scheduler(final TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void switchingMomentCalculation(final Device device) {
		logger.info("Switching moment calculation feature yet to implement.");
		// Switching moment calculation using schedules from device...
	}

}
