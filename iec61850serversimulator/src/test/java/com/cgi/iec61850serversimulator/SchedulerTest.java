package com.cgi.iec61850serversimulator;

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

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTest.class);

	// Initializing Scheduler components
	final ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
	final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
	// Scheduler scheduler = new Scheduler(this.taskScheduler);
}
